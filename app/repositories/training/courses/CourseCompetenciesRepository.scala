package repositories.training.courses
import com.datastax.driver.core.Row
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.dsl._
import com.websudos.phantom.keys.PartitionKey
import com.websudos.phantom.reactivestreams._
import conf.connection.DataConnection
import domain.training.courses.CourseCompetencies

import scala.concurrent.Future
/**
  * Created by SONY on 2016-10-19.
  */
class CourseCompetenciesRepository extends CassandraTable[CourseCompetenciesRepository,CourseCompetencies]{
  object organisationId extends StringColumn(this) with PartitionKey[String]
  object courseId extends StringColumn(this)
  object compentencyId extends StringColumn(this)

  override def fromRow(r: Row): CourseCompetencies = {
    CourseCompetencies(organisationId(r),courseId(r),compentencyId(r))
  }
}

object CourseCompetenciesRepository extends CourseCompetenciesRepository with RootConnector {
  override lazy val tableName = "coursecompetencies"

  override implicit def space: KeySpace = DataConnection.keySpace

  override implicit def session: Session = DataConnection.session

  def save(courseCompetencies: CourseCompetencies): Future[ResultSet] = {
    insert
      .value(_.organisationId, courseCompetencies.organisationId)
      .value(_.courseId, courseCompetencies.courseId)
      .value(_.compentencyId, courseCompetencies.compentencyId)
      .future()
  }

  def findById(organisationId: String):Future[Option[CourseCompetencies]] = {
    select.where(_.organisationId eqs organisationId).one()
  }
  def findAll: Future[Seq[CourseCompetencies]] = {
    select.fetchEnumerator() run Iteratee.collect()
  }

  def deleteById(organisationId:String): Future[ResultSet] = {
    delete.where(_.organisationId eqs organisationId).future()
  }
}
