package yaa.semantic.passes.fs6;

import org.objectweb.asm.Label;
import yaa.ast.VDefinition;
import yaa.pojos.GlobalData;
import yaa.pojos.VariableData;
import yaa.pojos.YaaField;

import java.util.ArrayList;

import static yaa.pojos.GlobalData.fs6;

public class F6VDef {
  public static void def(VDefinition def) {
    var name = def.name.content;
    var field = (YaaField) fs6.getSymbol(name);

    var variables = fs6.variables.peek();
    var data = field.data;

    var expValue = F6Utils.matchInfo(data, def.value);
    variables.store(data, name);

    var label = new Label();
    F6.mw().visitLabel(label);
    F6.mw().visitLineNumber(def.start.line, label);

    var index = variables.index;
    var dcp = data.descriptor();

    int index_into = dcp.equals("D") || dcp.equals("J") ? index - 1 : index;
    if (def.type == null) {
      F6.variableMeta.peek().add(
          new VariableData(
              name,
              label,
              dcp,
              data.clzUseSignature(),
              index_into,
              new ArrayList<>(0),
              new ArrayList<>(0)
          )
      );
    } else {
      F6.variableMeta.peek().add(
          new VariableData(
              name,
              label,
              dcp,
              data.clzUseSignature(),
              index_into,
              def.type.metaCalls,
              def.type.arguments
          )
      );
    }
  }
}