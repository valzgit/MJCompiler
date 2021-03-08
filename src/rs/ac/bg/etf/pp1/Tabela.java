package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.visitors.DumpSymbolTableVisitor;
import rs.etf.pp1.symboltable.visitors.SymbolTableVisitor;

public class Tabela extends Tab {
	public static final Struct boolType = new Struct(Struct.Bool);
	public static final Struct arrTypeInt = new Struct(Struct.Array);
	public static final Struct arrTypeChar = new Struct(Struct.Array);
	public static final Struct arrTypeBool = new Struct(Struct.Array);
	
	public static void init() {
		Tab.init();
		Scope universe = Tab.currentScope;
		universe.addToLocals(new Obj(Obj.Type,"bool",boolType));
		universe.addToLocals(new Obj(Obj.Type,"arrInt",arrTypeInt));
		arrTypeInt.setElementType(intType);
		universe.addToLocals(new Obj(Obj.Type,"arrChar",arrTypeChar));
		arrTypeChar.setElementType(charType);
		universe.addToLocals(new Obj(Obj.Type,"arrBool",arrTypeBool));
		arrTypeBool.setElementType(boolType);
//		Obj objekat=Tabela.find("ord");
//		objekat.setAdr(adr);
	}
	public static void dump() {
		dump(null);
	}
	public static void dump(SymbolTableVisitor stv) {
		System.out.println("=====================SYMBOL TABLE DUMP=========================");
		if (stv == null)
			stv = new DumpSymbolTableVisitorNovi();
		for (Scope s = currentScope; s != null; s = s.getOuter()) {
			s.accept(stv);
		}
		System.out.println(stv.getOutput());
	}

}
