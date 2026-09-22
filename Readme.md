# Kotlin To-Do List

Aplicativo Android de lista de tarefas desenvolvido em **Kotlin** com **Jetpack Compose**, como parte da atividade individual da FIAP sobre implementação da camada de UI, navegação e ViewModel.

O objetivo é permitir que o usuário **liste, crie, edite, conclua e exclua tarefas**, com persistência local através do Room e navegação entre a lista e o formulário de cadastro/edição.

## Tecnologias utilizadas

- **Kotlin**
- **Jetpack Compose** — construção de UI declarativa
- **Room** — persistência local em banco SQLite
- **Coroutines / Flow** — operações assíncronas e streams de dados reativos
- **ViewModel** — retenção de estado sobrevivente a mudanças de configuração
- **Navigation Compose** — navegação entre as telas do app

## Arquitetura

O projeto segue uma separação em camadas simples, inspirada no padrão **MVVM**:

```
data/            → Entidade (Tarefa), DAO e Database (Room)
repository/      → TarefaRepository
viewmodel/       → TarefaViewModel
ui/              → Telas em Compose (ListaTarefasScreen, FormularioTarefaScreen)
navigation/      → AppNavigation (rotas)
```

### TarefaRepository

Responsável por isolar a origem dos dados do restante do app. Ele recebe o `TarefaDao` (Room) e expõe:

- `tarefas: Flow<List<Tarefa>>` — stream reativo com todas as tarefas, direto do banco.
- `inserir(tarefa)`, `atualizar(tarefa)`, `deletar(tarefa)` — operações suspensas (`suspend`) que delegam ao DAO.

Ele existe para que a ViewModel (e o restante do app) nunca dependa diretamente do Room — se um dia a fonte de dados mudar (API remota, cache, etc.), só o Repository precisa ser alterado.

### TarefaViewModel

Faz a ponte entre o Repository e a UI. Principais responsabilidades:

- Converte o `Flow` do Repository em um `StateFlow` (`tarefas`) usando `stateIn`, com `SharingStarted.WhileSubscribed(5_000)` — assim a coleta do Flow só fica ativa enquanto há uma tela observando, evitando trabalho desnecessário em segundo plano.
- Expõe as funções `inserir`, `atualizar` e `deletar`, cada uma disparando uma coroutine em `viewModelScope` para não bloquear a thread principal.
- Possui uma **Factory** (`TarefaViewModel.factory(context)`) responsável por instanciar o `TarefaDatabase`, o `TarefaRepository` e, por fim, a própria ViewModel — usada na `MainActivity` para criar a instância corretamente.

### ListaTarefasScreen

Tela inicial do app. Observa o estado da lista de tarefas com:

```kotlin
val tarefas by viewModel.tarefas.collectAsStateWithLifecycle()
```

Isso garante que a UI só recompõe quando a lista muda, e que a coleta do `StateFlow` respeita o ciclo de vida da tela (pausa quando o app vai para segundo plano).

A partir do estado, a tela:
- Exibe as tarefas em uma **LazyColumn**, cada item em um `Card`.
- Permite marcar/desmarcar uma tarefa como concluída via `Checkbox`, disparando `viewModel.atualizar(...)`.
- Permite excluir uma tarefa pelo ícone de lixeira, disparando `viewModel.deletar(...)`.
- Ao clicar em um item, aciona `onEditarTarefa(tarefa.id)`, navegando para o formulário em modo de edição.
- Um `FloatingActionButton` aciona `onNovaTarefa`, navegando para o formulário em modo de cadastro.
- Inclui `@Preview` para o estado com tarefas, o estado vazio e os itens individuais (pendente/concluído).

### FormularioTarefaScreen

Tela única que atende tanto o **cadastro** quanto a **edição**, dependendo do `tarefaId` recebido:

- Se `tarefaId == 0`, o formulário abre vazio e, ao salvar, chama `viewModel.inserir(...)`.
- Se `tarefaId != 0`, a tela busca a tarefa correspondente na lista observada (`tarefas.find { it.id == tarefaId }`), pré-preenche os campos de título e descrição, e, ao salvar, chama `viewModel.atualizar(...)` com os dados atualizados.
- Após salvar, `onVoltar()` é chamado para retornar à tela anterior.
- Inclui `@Preview` para os dois cenários: nova tarefa e edição de tarefa existente.

### AppNavigation

Configurada com **Navigation Compose**, define duas rotas dentro de um `NavHost`:

- `"lista"` — tela inicial (`ListaTarefasScreen`).
- `"formulario/{tarefaId}"` — tela de formulário, recebendo o ID da tarefa como argumento de rota.

A navegação entre as telas acontece assim:

```kotlin
onNovaTarefa = { navController.navigate("formulario/0") }
onEditarTarefa = { id -> navController.navigate("formulario/$id") }
```

Usar `0` como valor padrão permite reaproveitar a mesma rota tanto para criar quanto para editar, apenas variando o argumento passado. Dentro da rota do formulário, o ID é extraído dos argumentos da back stack (`backStackEntry.arguments`) e convertido para `Int`.

### MainActivity

Ponto de entrada do app. Na criação da Activity:

1. Cria a `TarefaViewModel` através da sua `Factory` (`TarefaViewModel.factory(applicationContext)`), garantindo que ela já nasça conectada ao `TarefaRepository` e ao banco Room.
2. Chama `AppNavigation(viewModel = viewModel)` dentro do `setContent`, substituindo completamente o conteúdo padrão gerado pelo template do Android Studio.

## Como executar o projeto

1. Clone este repositório.
2. Abra a pasta do projeto no **Android Studio**.
3. Aguarde a sincronização do Gradle.
4. Selecione um emulador (ou conecte um dispositivo físico) e clique em **Run** (▶).
5. O app abre diretamente na tela de listagem de tarefas.

## Funcionalidades

- [x] Listar tarefas cadastradas
- [x] Cadastrar nova tarefa
- [x] Editar tarefa existente
- [x] Marcar/desmarcar tarefa como concluída
- [x] Excluir tarefa
- [x] Navegação entre lista e formulário
- [x] Persistência local com Room

## Evidências

<!-- Adicione aqui as imagens da pasta docs/evidencias, uma por funcionalidade -->

| Funcionalidade | Print |
|---|---|
| Tela inicial (lista vazia) | ![Lista vazia](evidencias/Tela%20inicial%20sem%20tarefas%20cadastradas.png) |
| Cadastro de nova tarefa | ![Cadastro](evidencias/Cadastro%20de%20tarefa.png) |
| Tarefa cadastrada aparecendo na lista | ![Tarefa criada](evidencias/Tela%20inicial.png) |
| Edição de tarefa existente | ![Edição](evidencias/Editando%20tarefa%20existente.png) |
| Tarefa marcada como concluída | ![Concluída](evidencias/Tarefa%20marcada%20como%20conclu%C3%ADda.png) |
| Exclusão de tarefa | ![Exclusão](evidencias/Exclus%C3%A3o%20de%20tarefa.png) |
| Navegação entre lista e formulário | ![Navegação](evidencias/Cadastro%20de%20tarefa.png) |
| Build/execução sem erros | ![Build](evidencias/Build%20-%20execu%C3%A7%C3%A3o%20sem%20erros.png) |

## Autor

Desenvolvido por FeFreitas06 como atividade individual — FIAP.