package FeFreitas06.com.github.kotlin_todolist.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TarefaDaoTest {

    private lateinit var database: TarefaDatabase
    private lateinit var dao: TarefaDao

    @Before
    fun criarBanco() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TarefaDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.tarefaDao()
    }

    @After
    fun fecharBanco() {
        database.close()
    }

    @Test
    fun inserirTarefaEListar() = runTest {
        val tarefa = Tarefa(titulo = "Estudar Room", descricao = "Aprender Entity e DAO")
        dao.inserir(tarefa)

        val tarefas = dao.listarTodas().first()
        Assert.assertEquals(1, tarefas.size)
        Assert.assertEquals("Estudar Room", tarefas[0].titulo)
        Assert.assertFalse(tarefas[0].concluida)
    }


    @Test
    fun marcarTarefaComoConcluida() = runTest {
        dao.inserir(Tarefa(titulo = "Tarefa 1", descricao = ""))
        val inserida = dao.listarTodas().first().first()

        dao.atualizar(inserida.copy(concluida = true))

        val atualizada = dao.listarTodas().first().first()
        Assert.assertTrue(atualizada.concluida)
    }

    @Test
    fun deletarTarefa() = runTest {
        dao.inserir(Tarefa(titulo = "Para deletar", descricao = ""))
        val inserida = dao.listarTodas().first().first()

        dao.deletar(inserida)

        val tarefas = dao.listarTodas().first()
        Assert.assertTrue(tarefas.isEmpty())
    }
}