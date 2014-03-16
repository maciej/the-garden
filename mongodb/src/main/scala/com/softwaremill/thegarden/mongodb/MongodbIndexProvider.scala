package com.softwaremill.thegarden.mongodb

trait MongodbIndexProvider {

  def collectionName: String

  def ensureIndexes()

}
