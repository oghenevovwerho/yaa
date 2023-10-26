package yaa.semantic.passes.fs6.f6utils;

import yaa.semantic.passes.fs6.F6Utils;
import yaa.pojos.GlobalData;

public class F6CastUtils {
  public static String castBoxedToBoxed(String from, String to) {
    if (from.equals(to)) {
      return from;
    }
    switch (from) {
      case GlobalData.double$boxed -> {
        switch (to) {
          case GlobalData.int$boxed -> {
            F6Utils.unBox(GlobalData.double$boxed);
            F6Utils.castTo(GlobalData.double$name, GlobalData.int$name);
            F6Utils.boxPrimitive(GlobalData.int$name);
          }
          case GlobalData.long$boxed -> {
            F6Utils.unBox(GlobalData.double$boxed);
            F6Utils.castTo(GlobalData.double$name, GlobalData.long$name);
            F6Utils.boxPrimitive(GlobalData.long$name);
          }
          case GlobalData.float$boxed -> {
            F6Utils.unBox(GlobalData.double$boxed);
            F6Utils.castTo(GlobalData.double$name, GlobalData.float$name);
            F6Utils.boxPrimitive(GlobalData.float$name);
          }
          case GlobalData.char$boxed -> {
            F6Utils.unBox(GlobalData.double$boxed);
            F6Utils.castTo(GlobalData.double$name, GlobalData.char$name);
            F6Utils.boxPrimitive(GlobalData.char$name);
          }
          case GlobalData.byte$boxed -> {
            F6Utils.unBox(GlobalData.double$boxed);
            F6Utils.castTo(GlobalData.double$name, GlobalData.byte$name);
            F6Utils.boxPrimitive(GlobalData.byte$name);
          }
          case GlobalData.short$boxed -> {
            F6Utils.unBox(GlobalData.double$boxed);
            F6Utils.castTo(GlobalData.double$name, GlobalData.short$name);
            F6Utils.boxPrimitive(GlobalData.short$name);
          }
        }
      }
      case GlobalData.float$boxed -> {
        switch (to) {
          case GlobalData.int$boxed -> {
            F6Utils.unBox(GlobalData.float$boxed);
            F6Utils.castTo(GlobalData.float$name, GlobalData.int$name);
            F6Utils.boxPrimitive(GlobalData.int$name);
          }
          case GlobalData.double$boxed -> {
            F6Utils.unBox(GlobalData.float$boxed);
            F6Utils.castTo(GlobalData.float$name, GlobalData.double$name);
            F6Utils.boxPrimitive(GlobalData.double$name);
          }
          case GlobalData.long$boxed -> {
            F6Utils.unBox(GlobalData.float$boxed);
            F6Utils.castTo(GlobalData.float$name, GlobalData.long$name);
            F6Utils.boxPrimitive(GlobalData.long$name);
          }
          case GlobalData.char$boxed -> {
            F6Utils.unBox(GlobalData.float$boxed);
            F6Utils.castTo(GlobalData.float$name, GlobalData.char$name);
            F6Utils.boxPrimitive(GlobalData.char$name);
          }
          case GlobalData.byte$boxed -> {
            F6Utils.unBox(GlobalData.float$boxed);
            F6Utils.castTo(GlobalData.float$name, GlobalData.byte$name);
            F6Utils.boxPrimitive(GlobalData.byte$name);
          }
          case GlobalData.short$boxed -> {
            F6Utils.unBox(GlobalData.float$boxed);
            F6Utils.castTo(GlobalData.float$name, GlobalData.short$name);
            F6Utils.boxPrimitive(GlobalData.short$name);
          }
        }
      }
      case GlobalData.long$boxed -> {
        switch (to) {
          case GlobalData.int$boxed -> {
            F6Utils.unBox(GlobalData.long$boxed);
            F6Utils.castTo(GlobalData.long$name, GlobalData.int$name);
            F6Utils.boxPrimitive(GlobalData.int$name);
          }
          case GlobalData.double$boxed -> {
            F6Utils.unBox(GlobalData.long$boxed);
            F6Utils.castTo(GlobalData.long$name, GlobalData.double$name);
            F6Utils.boxPrimitive(GlobalData.double$name);
          }
          case GlobalData.float$boxed -> {
            F6Utils.unBox(GlobalData.long$boxed);
            F6Utils.castTo(GlobalData.long$name, GlobalData.float$name);
            F6Utils.boxPrimitive(GlobalData.float$name);
          }
          case GlobalData.char$boxed -> {
            F6Utils.unBox(GlobalData.long$boxed);
            F6Utils.castTo(GlobalData.long$name, GlobalData.char$name);
            F6Utils.boxPrimitive(GlobalData.char$name);
          }
          case GlobalData.byte$boxed -> {
            F6Utils.unBox(GlobalData.long$boxed);
            F6Utils.castTo(GlobalData.long$name, GlobalData.byte$name);
            F6Utils.boxPrimitive(GlobalData.byte$name);
          }
          case GlobalData.short$boxed -> {
            F6Utils.unBox(GlobalData.long$boxed);
            F6Utils.castTo(GlobalData.long$name, GlobalData.short$name);
            F6Utils.boxPrimitive(GlobalData.short$name);
          }
        }
      }
      case GlobalData.int$boxed -> {
        switch (to) {
          case GlobalData.long$boxed -> {
            F6Utils.unBox(GlobalData.int$boxed);
            F6Utils.castTo(GlobalData.int$name, GlobalData.long$name);
            F6Utils.boxPrimitive(GlobalData.long$name);
          }
          case GlobalData.double$boxed -> {
            F6Utils.unBox(GlobalData.int$boxed);
            F6Utils.castTo(GlobalData.int$name, GlobalData.double$name);
            F6Utils.boxPrimitive(GlobalData.double$name);
          }
          case GlobalData.float$boxed -> {
            F6Utils.unBox(GlobalData.int$boxed);
            F6Utils.castTo(GlobalData.int$name, GlobalData.float$name);
            F6Utils.boxPrimitive(GlobalData.float$name);
          }
          case GlobalData.char$boxed -> {
            F6Utils.unBox(GlobalData.int$boxed);
            F6Utils.castTo(GlobalData.int$name, GlobalData.char$name);
            F6Utils.boxPrimitive(GlobalData.char$name);
          }
          case GlobalData.byte$boxed -> {
            F6Utils.unBox(GlobalData.int$boxed);
            F6Utils.castTo(GlobalData.int$name, GlobalData.byte$name);
            F6Utils.boxPrimitive(GlobalData.byte$name);
          }
          case GlobalData.short$boxed -> {
            F6Utils.unBox(GlobalData.int$boxed);
            F6Utils.castTo(GlobalData.int$name, GlobalData.short$name);
            F6Utils.boxPrimitive(GlobalData.short$name);
          }
        }
      }
      case GlobalData.char$boxed -> {
        switch (to) {
          case GlobalData.long$boxed -> {
            F6Utils.unBox(GlobalData.char$boxed);
            F6Utils.castTo(GlobalData.char$name, GlobalData.long$name);
            F6Utils.boxPrimitive(GlobalData.long$name);
          }
          case GlobalData.double$boxed -> {
            F6Utils.unBox(GlobalData.char$boxed);
            F6Utils.castTo(GlobalData.char$name, GlobalData.double$name);
            F6Utils.boxPrimitive(GlobalData.double$name);
          }
          case GlobalData.float$boxed -> {
            F6Utils.unBox(GlobalData.char$boxed);
            F6Utils.castTo(GlobalData.char$name, GlobalData.float$name);
            F6Utils.boxPrimitive(GlobalData.float$name);
          }
          case GlobalData.short$boxed -> {
            F6Utils.unBox(GlobalData.char$boxed);
            F6Utils.castTo(GlobalData.char$name, GlobalData.short$name);
            F6Utils.boxPrimitive(GlobalData.short$name);
          }
          case GlobalData.byte$boxed -> {
            F6Utils.unBox(GlobalData.char$boxed);
            F6Utils.castTo(GlobalData.char$name, GlobalData.byte$name);
            F6Utils.boxPrimitive(GlobalData.byte$name);
          }
        }
      }
      case GlobalData.short$boxed -> {
        switch (to) {
          case GlobalData.long$boxed -> {
            F6Utils.unBox(GlobalData.short$boxed);
            F6Utils.castTo(GlobalData.short$name, GlobalData.long$name);
            F6Utils.boxPrimitive(GlobalData.long$name);
          }
          case GlobalData.double$boxed -> {
            F6Utils.unBox(GlobalData.short$boxed);
            F6Utils.castTo(GlobalData.short$name, GlobalData.double$name);
            F6Utils.boxPrimitive(GlobalData.double$name);
          }
          case GlobalData.float$boxed -> {
            F6Utils.unBox(GlobalData.short$boxed);
            F6Utils.castTo(GlobalData.short$name, GlobalData.float$name);
            F6Utils.boxPrimitive(GlobalData.float$name);
          }
          case GlobalData.char$boxed -> {
            F6Utils.unBox(GlobalData.short$boxed);
            F6Utils.castTo(GlobalData.short$name, GlobalData.char$name);
            F6Utils.boxPrimitive(GlobalData.char$name);
          }
          case GlobalData.byte$boxed -> {
            F6Utils.unBox(GlobalData.short$boxed);
            F6Utils.castTo(GlobalData.short$name, GlobalData.byte$name);
            F6Utils.boxPrimitive(GlobalData.byte$name);
          }
        }
      }
      case GlobalData.byte$boxed -> {
        switch (to) {
          case GlobalData.long$boxed -> {
            F6Utils.unBox(GlobalData.byte$boxed);
            F6Utils.castTo(GlobalData.byte$name, GlobalData.long$name);
            F6Utils.boxPrimitive(GlobalData.long$name);
          }
          case GlobalData.double$boxed -> {
            F6Utils.unBox(GlobalData.byte$boxed);
            F6Utils.castTo(GlobalData.byte$name, GlobalData.double$name);
            F6Utils.boxPrimitive(GlobalData.double$name);
          }
          case GlobalData.float$boxed -> {
            F6Utils.unBox(GlobalData.byte$boxed);
            F6Utils.castTo(GlobalData.byte$name, GlobalData.float$name);
            F6Utils.boxPrimitive(GlobalData.float$name);
          }
          case GlobalData.char$boxed -> {
            F6Utils.unBox(GlobalData.byte$boxed);
            F6Utils.castTo(GlobalData.byte$name, GlobalData.char$name);
            F6Utils.boxPrimitive(GlobalData.char$name);
          }
          case GlobalData.short$boxed -> {
            F6Utils.unBox(GlobalData.byte$boxed);
            F6Utils.castTo(GlobalData.byte$name, GlobalData.short$name);
            F6Utils.boxPrimitive(GlobalData.short$name);
          }
        }
      }
    }
    return to;
  }
}