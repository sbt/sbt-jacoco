package de.johoop.jacoco4sbt.filter

object ScalaSyntheticMethod {
  def isSyntheticMethod(owner: String, name: String, firstLine: Int, lastLine: Int) = {
    val isModuleClass = owner.endsWith("$")
    val isOneLiner = firstLine == lastLine
    isOneLiner && (
         (isModuleClass && isSyntheticObjectMethodName(name))
      || isSyntheticInstanceMethodName(name)
    )
  }

  private def isSyntheticInstanceMethodName(name: String): Boolean = isCaseInstanceMethod(name)
  private def isSyntheticObjectMethodName(name: String): Boolean = isCaseCompanionMethod(name) || isAnyValCompanionMethod(name)

  private def isCaseInstanceMethod(name: String) = name match {
    case "canEqual" | "copy" | "equals" | "hashCode" |"productPrefix" |
         "productArity" | "productElement" | "productIterator" | "toString" => true
    case _ if name.startsWith("copy$default") => true
    case _ => false
  }
  private def isCaseCompanionMethod(name: String) = name match {
    case "apply" | "unapply" | "unapplySeq" | "readResolve" => true
    case _ => false
  }
  private def isAnyValCompanionMethod(name: String) = name match {
    case "equals$extension" | "hashCode$extension" => true
    case _ => false
  }
}
