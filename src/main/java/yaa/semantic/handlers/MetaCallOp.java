package yaa.semantic.handlers;

import yaa.ast.*;
import yaa.parser.YaaToken;
import yaa.pojos.YaaClz;
import yaa.pojos.YaaError;
import yaa.pojos.YaaMeta;
import yaa.semantic.passes.fs5.F5NewMeta;

import java.lang.annotation.ElementType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static yaa.pojos.GlobalData.*;
import static yaa.pojos.TypeCategory.enum_c;

public class MetaCallOp {
  public static Map<String, YaaMeta> metaCalls(List<YaaMetaCall> calls, ElementType kindOfUsage) {
    var metas = new HashMap<String, YaaMeta>(calls.size());
    var alreadyCalledMetas = new HashMap<String, YaaMetaCall>(1);
    for (var annotation : calls) {
      var meta = metaCall(annotation);
      if (!meta.allowedPlaces.contains(kindOfUsage) && !meta.allowedPlaces.isEmpty()) {
        throw new YaaError(
            annotation.placeOfUse(),
            meta.name + " cannot be used in " + kindOfUsage,
            "it can be used in " + meta.allowedPlaces
        );
      }
      var defined_meta = alreadyCalledMetas.get(meta.name);
      if (defined_meta != null && !meta.isRepeatable) {
        throw new YaaError(annotation.placeOfUse(), meta.name
            + " has already been called at " + defined_meta.placeOfUse(),
            meta.name + " is not repeatable"
        );
      }
      metas.put(annotation.name.content, meta);
      alreadyCalledMetas.put(meta.name, annotation);
    }
    return metas;
  }

  public static void metaCalls(ObjectType type, ElementType kindOfUsage) {
    metaCalls(type.metaCalls, kindOfUsage);
    if (type.hasInternalMeta) {
      for (var type_arg : type.arguments) {
        metaCalls(type_arg, ElementType.TYPE_USE);
      }
    }
  }

  private static YaaMeta metaCall(YaaMetaCall metaCall) {
    var call_name = metaCall.name.content;
    var gottenMeta = fs.getSymbol(call_name);
    if (gottenMeta instanceof YaaMeta meta) {
      if (meta.name.equals(configMetaClzName)) {
        return meta;
      }
      var given_keys = new HashMap<String, YaaToken>(metaCall.arguments.size());
      for (var arg : metaCall.arguments.entrySet()) {
        var field_name = arg.getKey().content;
        if (given_keys.get(field_name) != null) {
          throw new YaaError(
              arg.getKey().placeOfUse(),
              field_name + " is already defined at "
                  + given_keys.get(field_name).placeOfUse()
          );
        }
        var value = arg.getValue().visit(fs);
        var required_field = meta.requiredFields.get(field_name);
        if (required_field == null) {
          var default_field = meta.defaultFields.get(field_name);
          if (default_field == null) {
            throw new YaaError(
                arg.getKey().placeOfUse(),
                field_name + " is not defined in " + meta.name,
                "the defined keys are, required: " +
                    meta.requiredFields.keySet() + " and default: " + meta.defaultFields.keySet()
            );
          } else {
            if (!value.name.equals(default_field.data.name)) {
              throw new YaaError(
                  arg.getValue().placeOfUse(), default_field.data.name, value.name,
                  "The value given to \""
                      + arg.getKey().content + "\" and the one declared must match"
              );
            } else {
              if (value.name.equals(array$name)) {
                var declared_array = (YaaClz) default_field.data;
                var dec_inputted = declared_array.inputted.get(0);
                var given_array = (YaaClz) value;
                var giv_inputted = given_array.inputted.get(0);
                if (!dec_inputted.name.equals(giv_inputted.name)) {
                  throw new YaaError(
                      arg.getValue().placeOfUse(), default_field.data.toString(), value.name,
                      "The value given to \""
                          + arg.getKey().content + "\" and the one declared must match"
                  );
                }
              }
            }
          }
        } else {
          if (!value.name.equals(required_field.data.name)) {
            throw new YaaError(
                arg.getValue().placeOfUse(), required_field.data.toString(), value.name,
                "The value given to \""
                    + arg.getKey().content + "\" and the one declared must match"
            );
          } else {
            if (value.name.equals(array$name)) {
              var declared_array = (YaaClz) required_field.data;
              var dec_inputted = declared_array.inputted.get(0);
              var given_array = (YaaClz) value;
              var giv_inputted = given_array.inputted.get(0);
              if (!dec_inputted.name.equals(giv_inputted.name)) {
                throw new YaaError(
                    arg.getValue().placeOfUse(),
                    required_field.data.toString() + " was original declared",
                    giv_inputted + " was given instead",
                    "The value given to \""
                        + arg.getKey().content + "\" and the one declared must match"
                );
              }
            }
          }
        }
        given_keys.put(field_name, arg.getKey());
        if (value instanceof YaaClz clz) {
          if (F5NewMeta.isPermissibleMetaValue(clz)) {
            if (clz.name.equals(array$name)) {
              var array_ast = (VCall) arg.getValue();
              var array_inputted = clz.inputted.get(0);
              var array_inputted_name = array_inputted.name;
              if (array_inputted.category == enum_c) {
                for (var array_item : array_ast.arguments) {
                  if (!(array_item instanceof Name || array_item instanceof VGet)) {
                    throw new YaaError(
                        array_item.placeOfUse(),
                        "The enum values given to an annotation array " +
                            "must be a literal name or a static field access"
                    );
                  }
                }
              } else if (array_inputted_name.equals(double$name)) {
                for (var array_item : array_ast.arguments) {
                  if (!(array_item instanceof Pointed)) {
                    throw new YaaError(
                        array_item.placeOfUse(),
                        "The value given as an annotation's "
                            + array_inputted_name +
                            " must be a constant of " + array_inputted_name
                    );
                  }
                }
              } else if (array_inputted_name.equals(float$name)) {
                for (var array_item : array_ast.arguments) {
                  if (!(array_item instanceof Floated)) {
                    throw new YaaError(
                        array_item.placeOfUse(),
                        "The value given as an annotation's "
                            + array_inputted_name +
                            " must be a constant of " + array_inputted_name
                    );
                  }
                }
              } else if (array_inputted_name.equals(long$name)) {
                for (var array_item : array_ast.arguments) {
                  if (!(array_item instanceof Longed)) {
                    throw new YaaError(
                        array_item.placeOfUse(),
                        "The value given as an annotation's "
                            + array_inputted_name +
                            " must be a constant of " + array_inputted_name
                    );
                  }
                }
              } else if (array_inputted_name.equals(int$name)) {
                for (var array_item : array_ast.arguments) {
                  if (!(array_item instanceof Decimal)) {
                    throw new YaaError(
                        array_item.placeOfUse(),
                        "The value given as an annotation's "
                            + array_inputted_name +
                            " must be a constant of " + array_inputted_name
                    );
                  }
                }
              } else if (array_inputted_name.equals(short$name)) {
                for (var array_item : array_ast.arguments) {
                  if (!(array_item instanceof Shorted)) {
                    throw new YaaError(
                        array_item.placeOfUse(),
                        "The value given as an annotation's "
                            + array_inputted_name +
                            " must be a constant of " + array_inputted_name
                    );
                  }
                }
              } else if (array_inputted_name.equals(byte$name)) {
                for (var array_item : array_ast.arguments) {
                  if (!(array_item instanceof Byted)) {
                    throw new YaaError(
                        array_item.placeOfUse(),
                        "The value given as an annotation's "
                            + array_inputted_name +
                            " must be a constant of " + array_inputted_name
                    );
                  }
                }
              } else if (array_inputted_name.equals(char$name)) {
                for (var array_item : array_ast.arguments) {
                  if (!(array_item instanceof Cha)) {
                    throw new YaaError(
                        array_item.placeOfUse(),
                        "The value given as an annotation's "
                            + array_inputted_name +
                            " must be a constant of " + array_inputted_name
                    );
                  }
                }
              } else if (array_inputted_name.equals(boole$name)) {
                for (var array_item : array_ast.arguments) {
                  if (!(array_item instanceof True || array_item instanceof False)) {
                    throw new YaaError(
                        array_item.placeOfUse(),
                        "The value given as an annotation's "
                            + array_inputted_name +
                            " must be a constant of " + array_inputted_name
                    );
                  }
                }
              }
            }
          } else {
            throw new YaaError(
                arg.getValue().placeOfUse(),
                "The value of an annotations field must be one of the following",
                "A String", "A primitive", "An enum", "An array of the above"
            );
          }
        } else {
          throw new YaaError(
              arg.getValue().placeOfUse(),
              "The value of an annotations field must be one of the following",
              "A String", "A primitive", "An enum", "An array of the above"
          );
        }
      }

      var not_given = new ArrayList<String>();

      for (var field_name : meta.requiredFields.keySet()) {
        if (given_keys.get(field_name) == null) {
          not_given.add(field_name);
        }
      }

      if (not_given.size() > 0) {
        throw new YaaError(
            metaCall.placeOfUse(),
            "The annotation fields below must be given a value",
            not_given.toString()
        );
      }
      return meta;
    } else {
      throw new YaaError(
          metaCall.placeOfUse(), call_name + " must be a valid annotation"
      );
    }
  }
}
