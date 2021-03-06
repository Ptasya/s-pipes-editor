package cz.cvut.kbss.spipes.test.service

import java.io.FileNotFoundException
import java.util

import cz.cvut.kbss.spipes.model.spipes.{Module, ModuleType}
import cz.cvut.kbss.spipes.persistence.dao.ScriptDao
import cz.cvut.kbss.spipes.service.ScriptService
import org.junit.Assert._
import org.junit.Test
import org.mockito.Mockito.when
import org.springframework.beans.factory.annotation.Autowired

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{Failure, Random, Success}

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 27.08.2017.
  */
class ScriptServiceTest extends BaseServiceTestRunner {

  @Autowired
  private var dao: ScriptDao = _

  @Autowired
  private var service: ScriptService = _

  private val fileName = ""

  @Test
  def moduleTypesGotFailureFileNotFound: Unit = {
    when(dao.getModuleTypes(fileName)).thenReturn(Failure(new FileNotFoundException()))
    assertEquals(Right(None), service.getModuleTypes(fileName))
  }

  @Test
  def moduleTypesGotFailureOther: Unit = {
    val e = new IllegalArgumentException()
    when(dao.getModuleTypes(fileName)).thenReturn(Failure(e))
    assertEquals(Left(e), service.getModuleTypes(fileName))
  }

  @Test
  def moduleTypesGotNullSuccess: Unit = {
    when(dao.getModuleTypes(fileName)).thenReturn(Success(null))
    assertEquals(Right(None), service.getModuleTypes(fileName))
  }

  @Test
  def moduleTypesGotEmptySuccess: Unit = {
    when(dao.getModuleTypes(fileName)).thenReturn(Success(new util.LinkedList[ModuleType]()))
    assertEquals(Right(None), service.getModuleTypes(fileName))
  }

  @Test
  def moduleTypesGotNonEmptySuccess: Unit = {
    val l = new util.LinkedList[ModuleType]()
    val size = Random.nextInt(100) + 1
    Seq.fill(size)(0).foreach((_) =>
      l.add(new ModuleType()))
    when(dao.getModuleTypes(fileName)).thenReturn(Success(l))
    assertEquals(Right(Some(l.asScala)), service.getModuleTypes(fileName))
  }

  @Test
  def modulesGotFailureFileNotFound: Unit = {
    when(dao.getModules(fileName)).thenReturn(Failure(new FileNotFoundException()))
    assertEquals(Right(None), service.getModules(fileName))
  }

  @Test
  def modulesGotFailureOther: Unit = {
    val e = new IllegalArgumentException()
    when(dao.getModules(fileName)).thenReturn(Failure(e))
    assertEquals(Left(e), service.getModules(fileName))
  }

  @Test
  def modulesGotEmptySuccess: Unit = {
    when(dao.getModules(fileName)).thenReturn(Success(new util.LinkedList[Module]()))
    assertEquals(Right(None), service.getModules(fileName))
  }

  @Test
  def modulesGotNonEmptySuccess: Unit = {
    val l = new util.LinkedList[Module]()
    val size = Random.nextInt(100) + 1
    Seq.fill(size)(0).foreach((_) =>
      l.add(new Module()))
    when(dao.getModules(fileName)).thenReturn(Success(l))
    assertEquals(Right(Some(l.asScala)), service.getModules(fileName))
  }
}