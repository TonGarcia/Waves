package com.wavesplatform.it.transactions

import com.wavesplatform.it.util._
import com.wavesplatform.it.{IntegrationSuiteWithThreeAddresses, Node}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class MakeUniqueAssetTransactionSpecification(override val allNodes: Seq[Node]) extends IntegrationSuiteWithThreeAddresses {
  test("make unique assets transaction makes asset name unique") {
    val f = for {
      _ <- assertBalances(firstAddress, 100 waves, 100 waves)

      issuedAssetId <- sender.issue(firstAddress, "asset1", "description", 100000, 2, reissuable = true, fee = 10 waves).map(_.id)

      _ <- Future.traverse(allNodes)(_.waitForTransaction(issuedAssetId))

      _ <- assertBalances(firstAddress, 90 waves, 90 waves)
      _ <- assertAssetBalance(firstAddress, issuedAssetId, 100000)

      makeUnique <- sender.makeUnique(firstAddress, issuedAssetId, 10 waves).map(_.id)

      _ <- Future.traverse(allNodes)(_.waitForTransaction(makeUnique))

      _ <- assertBalances(firstAddress, 80 waves, 80 waves)
      _ <- assertAssetBalance(firstAddress, issuedAssetId, 100000)
    } yield succeed

    Await.result(f, 1 minute)
  }

  test("can't make unique not existed asset") {
    val f = for {
      _ <- assertRequestError(sender.makeUnique(firstAddress, "91MxUYbum9hrpJUcRwVe4no36ViqnQGAUaSmM8V8L8Jx", 10 waves).map(_.id))
    } yield succeed

    Await.result(f, 1 minute)
  }

  test("can't make unique assets issued by other address") {
    val f = for {
      _ <- assertBalances(firstAddress, 80 waves, 80 waves)

      issuedAssetId <- sender.issue(firstAddress, "asset4", "description", 100000, 2, reissuable = true, fee = 10 waves).map(_.id)

      _ <- Future.traverse(allNodes)(_.waitForTransaction(issuedAssetId))

      _ <- assertBalances(firstAddress, 70 waves, 70 waves)
      _ <- assertAssetBalance(firstAddress, issuedAssetId, 100000)

      _ <- assertRequestError(sender.makeUnique(thirdAddress, issuedAssetId, 10 waves))
    } yield succeed

    Await.result(f, 1 minute)
  }

  test("make unique assets transaction can be applied only once") {
    val f = for {
      _ <- assertBalances(firstAddress, 70 waves, 70 waves)

      issuedAssetId <- sender.issue(firstAddress, "asset2", "description", 100000, 2, reissuable = true, fee = 10 waves).map(_.id)
      _ <- Future.traverse(allNodes)(_.waitForTransaction(issuedAssetId))

      secondIssuedAssetId <- sender.issue(firstAddress, "asset2", "description", 100000, 2, reissuable = true, fee = 10 waves).map(_.id)
      _ <- Future.traverse(allNodes)(_.waitForTransaction(secondIssuedAssetId))

      _ <- assertBalances(firstAddress, 50 waves, 50 waves)
      _ <- assertAssetBalance(firstAddress, issuedAssetId, 100000)
      _ <- assertAssetBalance(firstAddress, secondIssuedAssetId, 100000)

      makeUniqueId <- sender.makeUnique(firstAddress, secondIssuedAssetId, 10 waves).map(_.id)

      _ <- Future.traverse(allNodes)(_.waitForTransaction(makeUniqueId))

      _ <- assertBalances(firstAddress, 40 waves, 40 waves)

      _ <- assertRequestError(sender.makeUnique(firstAddress, issuedAssetId, 10 waves))
      _ <- assertRequestError(sender.makeUnique(firstAddress, secondIssuedAssetId, 10 waves))
    } yield succeed

    Await.result(f, 1 minute)
  }

  test("make unique assets does not prohibit creating an asset with this name") {
    val f = for {
      _ <- assertBalances(firstAddress, 40 waves, 40 waves)

      issuedAssetId <- sender.issue(firstAddress, "asset3", "description", 100000, 2, reissuable = true, fee = 10 waves).map(_.id)

      _ <- Future.traverse(allNodes)(_.waitForTransaction(issuedAssetId))

      _ <- assertBalances(firstAddress, 30 waves, 30 waves)
      _ <- assertAssetBalance(firstAddress, issuedAssetId, 100000)

      makeUnique <- sender.makeUnique(firstAddress, issuedAssetId, 10 waves).map(_.id)

      _ <- Future.traverse(allNodes)(_.waitForTransaction(makeUnique))

      _ <- assertBalances(firstAddress, 20 waves, 20 waves)
      _ <- assertAssetBalance(firstAddress, issuedAssetId, 100000)

      secondIssueId <- sender.issue(firstAddress, "asset3", "description", 100000, 2, reissuable = true, fee = 10 waves).map(_.id)

      _ <- Future.traverse(allNodes)(_.waitForTransaction(secondIssueId))

      _ <- assertAssetBalance(firstAddress, secondIssueId, 100000)
    } yield succeed

    Await.result(f, 1 minute)
  }
}
