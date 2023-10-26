package yaa.ast;

import yaa.parser.YaaToken;

import java.util.ArrayList;

public class Import extends Stmt {
  public final ArrayList<YaaToken> tokens;
  public boolean is4Jvm;
  private YaaToken alias;
  public final String fullName;
  public final String refName;
  public final String owner;
  public final String afterOwner;

  public Import(ArrayList<YaaToken> tokens) {
    this.tokens = tokens;
    this.refName = tokens.get(tokens.size() - 1).content;
    if (tokens.size() == 2) {
      this.owner = tokens.get(0).content;
      this.afterOwner = tokens.get(1).content;
    } else {
      var sb = new StringBuilder();
      sb.append(tokens.get(0).content);
      int i = 1;
      for (; i < tokens.size() - 1; i++) {
        sb.append(".").append(tokens.get(i).content);
      }
      this.owner = sb.toString();
      this.afterOwner = tokens.get(i).content;
    }
    var sb = new StringBuilder();
    sb.append(tokens.get(0).content);
    for (int i = 1; i < tokens.size(); i++) {
      sb.append(".").append(tokens.get(i).content);
    }
    this.fullName = sb.toString();
  }

  public Import(ArrayList<YaaToken> tokens, YaaToken alias) {
    this.tokens = tokens;
    this.alias = alias;
    this.refName = alias.content;
    if (tokens.size() == 2) {
      this.owner = tokens.get(0).content;
      this.afterOwner = tokens.get(1).content;
    } else {
      var sb = new StringBuilder();
      sb.append(tokens.get(0).content);
      int i = 1;
      for (; i < tokens.size() - 1; i++) {
        sb.append(".").append(tokens.get(i).content);
      }
      this.afterOwner = tokens.get(i).content;
      this.owner = sb.toString();
    }
    var sb = new StringBuilder();
    sb.append(tokens.get(0).content);
    for (int i = 1; i < tokens.size(); i++) {
      sb.append(".").append(tokens.get(i).content);
    }
    this.fullName = sb.toString();
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    sb.append(tokens.get(0).content);
    for (int i = 1; i < tokens.size(); i++) {
      sb.append(".").append(tokens.get(i).content);
    }
    if (alias != null) {
      return sb.append(": ").append(alias.content).toString();
    }
    return sb.toString();
  }
}
