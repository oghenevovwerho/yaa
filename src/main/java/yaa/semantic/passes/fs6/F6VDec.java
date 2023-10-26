package yaa.semantic.passes.fs6;

import org.objectweb.asm.Label;
import yaa.ast.VDeclaration;
import yaa.pojos.GlobalData;
import yaa.pojos.VariableData;
import yaa.pojos.YaaField;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.fs6;
import static yaa.semantic.passes.fs6.F6Utils.loadDefaultData;

public class F6VDec {
  public static void dec(VDeclaration dec) {
    var name = dec.name.content;
    var field = (YaaField) fs6.getSymbol(name);

    var variables = fs6.variables.peek();
    var data = field.data;

    loadDefaultData(data.name);
    variables.store(data, name);

    var label = new Label();
    F6.mw().visitLabel(label);
    F6.mw().visitLineNumber(dec.start.line, label);

    var index = variables.index;
    var dcp = data.descriptor();

    int index_into = dcp.equals("D") || dcp.equals("J") ? index - 1 : index;
    if (dec.type.metaCalls.size() == 0) {
      F6.variableMeta.peek().add(
          new VariableData(
              name,
              label,
              dcp,
              data.clzUseSignature(),
              index_into,
              new ArrayList<>(0),
              dec.type.arguments
          ));
    } else {
      F6.variableMeta.peek().add(
          new VariableData(
              name,
              label,
              dcp,
              data.clzUseSignature(),
              index_into,
              dec.type.metaCalls,
              dec.type.arguments
          ));
    }
  }
}
