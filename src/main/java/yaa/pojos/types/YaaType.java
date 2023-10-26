package yaa.pojos.types;

public sealed class YaaType permits
    BoxedInt,
    BoxedLong,
    EnumClass,
    GenAbstract,
    GenClass,
    GenFuncInterface,
    GenInterface,
    PlainAbstract,
    PlainClass,
    PlainFuncInterface,
    PlainInterface,
    Primitive,
    RecordClass {
}