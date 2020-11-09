package frontend.components

import org.scalatest.funsuite.AnyFunSuite

class NumberFormatTest extends AnyFunSuite {

  test("amount") {
    assert(NumberFormat.amount(100e12) === "100.0 T")
    assert(NumberFormat.amount(10e12) === "10.0 T")
    assert(NumberFormat.amount(1e12) === "1.0 T")

    assert(NumberFormat.amount(100e6) === "100.0 M")
    assert(NumberFormat.amount(10e6) === "10.0 M")
    assert(NumberFormat.amount(1e6) === "1.0 M")

    assert(NumberFormat.amount(100e3) === "100.0 K")
    assert(NumberFormat.amount(10e3) === "10.0 K")
    assert(NumberFormat.amount(1e3) === "1.0 K")

    assert(NumberFormat.amount(100) === "100.0")
    assert(NumberFormat.amount(10) === "10.0")
    assert(NumberFormat.amount(1) === "1.0")
  }

}
