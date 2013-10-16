package de.johoop.jacoco4sbt.filter

import org.objectweb.asm.tree._
import org.objectweb.asm.Opcodes
import scala.collection.JavaConverters._
import Opcodes._

/** Detects accessor methods that do nothing other than load and return a field */
object AccessorDetector {
  def isAccessor(node: MethodNode): Boolean = {
    (node.instructions.size() < 10) && {
      val insn: List[AbstractInsnNode] = node.instructions.iterator().asInstanceOf[java.util.ListIterator[AbstractInsnNode]].asScala.toList
      val filtered = insn.filter {
        case _: LabelNode | _: LineNumberNode => false
        case _ => true
      }
      filtered.map(_.getOpcode) match {
        case ALOAD :: GETFIELD :: (IRETURN | LRETURN | FRETURN | DRETURN | ARETURN | RETURN) :: Nil => true
        case _ => false
      }
    }
  }
}
