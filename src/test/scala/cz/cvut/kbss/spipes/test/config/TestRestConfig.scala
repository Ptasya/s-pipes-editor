package cz.cvut.kbss.spipes.test.config

import cz.cvut.kbss.spipes.service.{FileWatcher, QAService, SpipesService, ViewService}
import org.mockito.Mockito
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter
import org.springframework.context.annotation.{Bean, ComponentScan, Configuration}
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 21.01.17.
  */
@Configuration
@ComponentScan(basePackages = Array("cz.cvut.kbss.spipes.rest"))
@EnableWebMvc
class TestRestConfig {

  @Bean
  def getspipesService: SpipesService = Mockito.mock(classOf[SpipesService])

  @Bean
  def getViewService: ViewService = Mockito.mock(classOf[ViewService])

  @Bean
  def getNodeService: QAService = Mockito.mock(classOf[QAService])

  @Bean
  def getFileWatcher: FileWatcher = Mockito.mock(classOf[FileWatcher])

  @Bean
  def mockBeanFactory = new MockBeanFactory

  class MockBeanFactory extends InstantiationAwareBeanPostProcessorAdapter {
    override def postProcessAfterInstantiation(bean: Any, beanName: String): Boolean =
      !Mockito.mockingDetails(bean).isMock
  }
}