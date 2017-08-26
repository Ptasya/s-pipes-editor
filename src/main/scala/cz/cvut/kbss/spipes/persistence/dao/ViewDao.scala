package cz.cvut.kbss.spipes.persistence.dao

import java.net.URI
import java.util.{Set => JSet}

import cz.cvut.kbss.spipes.model.view.{Edge, Node, View}
import org.springframework.stereotype.Repository

import scala.reflect.ClassTag
import scala.util.Try

/**
  * Created by Yan Doroshenko (yandoroshenko@protonmail.com) on 03.11.16.
  */
@Repository
class ViewDao extends AbstractDao[View] {

  def getViewNodes(uri: URI): Try[JSet[Node]] =
    get(uri).map(_.getNodes())

  def getViewEdges(uri: URI): Try[JSet[Edge]] =
    get(uri).map(_.getEdges())

  def updateView(uri: URI, other: View)(implicit tag: ClassTag[View]): Try[Unit] = {
    val em = emf.createEntityManager()
    get(uri).map { (v) =>
      em.getTransaction().begin()
      v.setLabel(other.getLabel)
      v.setNodes(other.getNodes)
      v.setEdges(other.getEdges)
      em.merge(v)
      em.getTransaction().commit()
    } match {
      case t: Try[Unit] =>
        em.close()
        t
    }
  }
}