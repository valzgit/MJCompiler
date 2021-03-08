package rs.ac.bg.etf.pp1;

import java.util.ArrayList;

import rs.ac.bg.etf.pp1.CounterVisitor.FormParamCounter;
import rs.ac.bg.etf.pp1.CounterVisitor.VarCounter;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {

	private int mainPc;

	public int getmainPc() {
		return mainPc;
	}

	public boolean prosledjenaprintsirina = false;
	public int pps = 0;

	public void visit(ProgName pn) {
		//hardcodovanje ord
		Obj objekat=Tabela.find("ord");
		objekat.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);
		Code.put(Code.load_n);
		Code.put(Code.exit);
		Code.put(Code.return_);
		//hardcodovanje chr
		objekat=Tabela.find("chr");
		objekat.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);
		Code.put(Code.load_n);
		Code.put(Code.exit);
		Code.put(Code.return_);
		//hardcodovanje len
		objekat=Tabela.find("len");
		objekat.setAdr(Code.pc);
		Code.put(Code.enter);
		Code.put(1);
		Code.put(1);
		Code.put(Code.load_n);
		Code.put(Code.arraylength);
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	public void visit(XMaybePrint xmp) {
		prosledjenaprintsirina = true;
		pps = xmp.getN1();
		// ako je manje od 0 vrati na 0
		if (pps < 0)
			pps = 0;
	}

	public void visit(StatementPrint printStmt) {
		if (printStmt.getExpr().struct == Tab.charType) {
			if (prosledjenaprintsirina == false) {
				Code.loadConst(1);
			} else {
				Code.loadConst(pps);
				prosledjenaprintsirina = false;
				pps = 0;
			}
			Code.put(Code.bprint);
		} else {
			if (prosledjenaprintsirina == false) {
				Code.loadConst(5);
			} else {
				Code.loadConst(pps);
				prosledjenaprintsirina = false;
				pps = 0;
			}
			Code.put(Code.print);
		}

	}

	public void visit(FactorNumConst cd) {
		Obj con = Tabela.insert(Obj.Con, "$", cd.struct);
		con.setLevel(0);
		con.setAdr(cd.getN1());
		Code.load(con);
	}

	public void visit(FactorCharConst fcc) {
		Obj con = Tabela.insert(Obj.Con, "$", fcc.struct);
		con.setLevel(0);
		con.setAdr(fcc.getC1().charAt(1));
		Code.load(con);
	}

	public void visit(FactorBooleanConst fbc) {
		Obj con = Tabela.insert(Obj.Con, "$", fbc.struct);
		con.setLevel(0);
		if (fbc.getBoolic().equals("true")) {
			con.setAdr(1);
		} else {
			con.setAdr(0);
		}
		Code.load(con);
	}

	public void visit(MethodVoidName mtn) {
		if ("main".equalsIgnoreCase(mtn.getMdvoid())) {
			mainPc = Code.pc;
		}
		mtn.obj.setAdr(Code.pc);
		// Dohvatanje argumenata i lokalnih varijabli
		SyntaxNode methodNode = mtn.getParent();

		VarCounter varCnt = new VarCounter();
		methodNode.traverseTopDown(varCnt);

		FormParamCounter fpCnt = new FormParamCounter();
		methodNode.traverseTopDown(fpCnt);

		// Generisi ulaz
		Code.put(Code.enter);
		Code.put(fpCnt.getCount());
		Code.put(fpCnt.getCount() + varCnt.getCount());

	}

	public void visit(MethodTypeName mtn) {

		mtn.obj.setAdr(Code.pc);
		// Dohvatanje argumenata i lokalnih varijabli
		SyntaxNode methodNode = mtn.getParent();

		VarCounter varCnt = new VarCounter();
		methodNode.traverseTopDown(varCnt);

		FormParamCounter fpCnt = new FormParamCounter();
		methodNode.traverseTopDown(fpCnt);

		// Generisi ulaz
		Code.put(Code.enter);
		Code.put(fpCnt.getCount());
		Code.put(fpCnt.getCount() + varCnt.getCount());

	}

	public void visit(MethodDeclVoid md) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(MethodDeclType mdt) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	// dodato za funkcije

	public void visit(DesStatAssign dsa) {
		if (factornew == true) {
			factornew = false;
			Code.put(Code.newarray);
			if (dsa.getDesignator().obj.getType() == Tabela.charType)
				Code.loadConst(0);
			else {
				Code.loadConst(1);
			}
		}

		if (indeksiraj.size() != 0) {
			 //System.out.println("Iz dodele: " + indeksiraj.get(indeksiraj.size() - 1));
			if (indeksiraj.get(indeksiraj.size() - 1) == true) {
				if (dsa.getDesignator().obj.getType() == Tabela.charType) {
					Code.put(Code.bastore);
				} else {
					Code.put(Code.astore);
				}
			} else {
				Code.store(dsa.getDesignator().obj);
			}
			indeksiraj.remove(indeksiraj.size() - 1);
		}

	}

	public ArrayList<Obj> staindeksiraj = new ArrayList<Obj>();

	public void visit(DesigIdent di) {
		staindeksiraj.add(di.obj);
		// System.out.println(di.getDesignatorName());
	}

	public ArrayList<Boolean> indeksiraj = new ArrayList<Boolean>();

	public void visit(XDesignatorList2 xdl2) {
		indeksiraj.add(true);
		//System.out.println("TRUE ");
	}

	public void visit(NoDesignatorList ndl) {

		if (ndl.getParent().getClass().equals(Designator.class)) {
			indeksiraj.add(false);
			//System.out.println("FALSE");
		} else {
			Code.load(staindeksiraj.get(staindeksiraj.size() - 1));
		}

		staindeksiraj.remove(staindeksiraj.size() - 1);
	}

	public void visit(StatementReturn sr) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}

	public void visit(DesStatMayActPars dsmap) {
		if (dsmap.getDesignator().getDesignatorIdent().obj.getKind() == Obj.Meth) {
			Obj funccall = dsmap.getDesignator().obj;
			int offset = funccall.getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(offset);
			if (dsmap.getDesignator().getDesignatorIdent().obj.getType() != Tabela.noType) {
				Code.put(Code.pop);
			}
			indeksiraj.remove(indeksiraj.size() - 1);
		}
	}

//////////////////////////////////////
	///////////////////////////////////////// dodao prvi deo na pocetku
	public void visit(FactorDesignator fdes) {

		if (fdes.getDesignator().getDesignatorIdent().obj.getKind() == Obj.Meth) {
			Obj funccall = fdes.getDesignator().getDesignatorIdent().obj;
			int offset = funccall.getAdr() - Code.pc;
			Code.put(Code.call);
			Code.put2(offset);
			indeksiraj.remove(indeksiraj.size() - 1);
			return;
		}
//////////////////////////////////////
/////////////////////////////////////////dodao prvi deo na pocetku	

		if (indeksiraj.get(indeksiraj.size() - 1) == false) {
			Code.load(fdes.getDesignator().obj);
			indeksiraj.remove(indeksiraj.size() - 1);
			 //System.out.println("IZBACIO FALSE "+ fdes.getDesignator().obj.getName());
		} else {
			if (fdes.getDesignator().obj.getType().equals(Tabela.charType))
				Code.put(Code.baload);
			else
				Code.put(Code.aload);
			indeksiraj.remove(indeksiraj.size() - 1);
			 //System.out.println("IZBACIO TRUE "+ fdes.getDesignator().obj.getName());
		}
	}

	// public ArrayList<Integer> mnozenje=new ArrayList<Integer>();
	public static final int MUL = 0;
	public static final int DIV = 1;
	public static final int MOD = 2;

	public int operacija = MUL;

	public void visit(MulopFactor mf) {
		switch (operacija) {
		case MUL:
			Code.put(Code.mul);
			break;
		case DIV:
			Code.put(Code.div);
			break;

		case MOD:
			Code.put(Code.rem);
			break;
		}
	}

	public void visit(Mul mul) {
		lastoperacija = MNOZENJE;
		operacija = MUL;
	}

	public void visit(Div d) {
		lastoperacija = MNOZENJE;
		operacija = DIV;
	}

	public void visit(Mod m) {
		lastoperacija = MNOZENJE;
		operacija = MOD;
	}

	public void visit(XMaybeAddopTerms xmat) {
		if (sabiraj)
			Code.put(Code.add);
		else
			Code.put(Code.sub);
	}

	public boolean sabiraj = true;

	public void visit(Add add) {
		lastoperacija = SABIRANJE;
		sabiraj = true;
	}

	public void visit(Sub sub) {
		lastoperacija = SABIRANJE;
		sabiraj = false;
	}

	public int delayedoperation = -1;
	public int lastoperacija = -1;
	public static final int MNOZENJE = 0;
	public static final int SABIRANJE = 1;
	// public boolean lastsabiraj=sabiraj;
	// public int lastmnozi=operacija;
	public ArrayList<Boolean> lastsabiraj = new ArrayList<Boolean>();
	public ArrayList<Integer> lastmnozi = new ArrayList<Integer>();

	public void visit(FactorExpr fe) {
		if (lastmnozi.size() != 0) {
			operacija = lastmnozi.get(lastmnozi.size() - 1);
			sabiraj = lastsabiraj.get(lastsabiraj.size() - 1);
			lastmnozi.remove(lastmnozi.size() - 1);
			lastsabiraj.remove(lastsabiraj.size() - 1);
		}
	}

	public void visit(Izdvojenalzagrada il) {
		if (lastoperacija != -1) {
			delayedoperation = lastoperacija;
			lastmnozi.add(operacija);
			lastsabiraj.add(sabiraj);
		}
	}

	public ArrayList<Boolean> maybesub = new ArrayList<Boolean>();

	public void visit(XMaybeSub xms) {
		Obj con = Tabela.insert(Obj.Con, "$", Tabela.intType);
		con.setLevel(0);
		con.setAdr(0);
		Code.load(con);
		maybesub.add(true);
	}

	public void visit(NotMaybeSub nms) {
		maybesub.add(false);
	}

	public void visit(Term e1) {
		if (e1.getParent().getClass() == Expr1.class) {
			if (maybesub.size() != 0) {
				if (maybesub.get(maybesub.size() - 1) == true)
					Code.put(Code.sub);

				maybesub.remove(maybesub.size() - 1);
			}
		}
	}

	public void visit(DesStatINC dsi) {
		if (indeksiraj.get(indeksiraj.size() - 1) == false) {
			Code.load(dsi.getDesignator().obj);
			Code.loadConst(1);
			Code.put(Code.add);
			Code.store(dsi.getDesignator().obj);
			// System.out.println("IZBACIO FALSE INCREMENT");
		} else {
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.loadConst(1);
			Code.put(Code.add);
			Code.put(Code.astore);
			// System.out.println("IZBACIO TRUE INCREMENT");
		}
		indeksiraj.remove(indeksiraj.size() - 1);
	}

	public void visit(DesStatDEC dsd) {
		if (indeksiraj.get(indeksiraj.size() - 1) == false) {
			Code.load(dsd.getDesignator().obj);
			Code.loadConst(1);
			Code.put(Code.sub);
			Code.store(dsd.getDesignator().obj);
			// System.out.println("IZBACIO FALSE DECREMENT");
		} else {
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.loadConst(1);
			Code.put(Code.sub);
			Code.put(Code.astore);
			// System.out.println("IZBACIO TRUE DECREMENT");
		}
		indeksiraj.remove(indeksiraj.size() - 1);
	}

	boolean factornew = false;

	public void visit(FactorNEW fnew) {
		factornew = true;
	}

	public int PCcurrent = 0;

	public void visit(UslovTernarni ut) {
		PCcurrent = Code.pc + 2;
		Code.loadConst(0);
		Code.putFalseJump(Code.ne, 0);
	}

	public int PCcurrent2 = 0;

	public void visit(PrviTernarni ut) {
		PCcurrent2 = Code.pc + 1;
		Code.putJump(0);
		Code.fixup(PCcurrent);
	}

	public void visit(DrugiTernarni ut) {
		Code.fixup(PCcurrent2);
	}

	public void visit(StatementRead sr) {
		Struct s = sr.getDesignator().obj.getType();
		if (s == Tabela.charType) {
			Code.put(Code.bread);
		} else {
			Code.put(Code.read);
		}
		if (indeksiraj.size() != 0) {
			if (indeksiraj.get(indeksiraj.size() - 1) == true) {
				if (sr.getDesignator().obj.getType() == Tabela.charType) {
					Code.put(Code.bastore);
				} else {
					Code.put(Code.astore);
				}
			} else {
				Code.store(sr.getDesignator().obj);
			}
			indeksiraj.remove(indeksiraj.size() - 1);
		}
	}

	/////////////////////////////////////////// FAZA B
	/////////////////////////////////////////// KOD///////////////////////////////////////////////////
	public static final int EQ = 0;
	public static final int NEQ = 1;
	public static final int G = 4;
	public static final int GE = 5;
	public static final int L = 2;
	public static final int LE = 3;
	public int relopoperacija = EQ;

	public ArrayList<Integer> pcuslovi = new ArrayList<Integer>();
	public ArrayList<Integer> poslednjipcuslovi = new ArrayList<Integer>();
	public ArrayList<Integer> poslednjalista = new ArrayList<Integer>();
	public ArrayList<Integer> komestapripada = new ArrayList<Integer>();
	public boolean biocondition=true;
	public void visit(CondTerm ct) {
		if(biocondition==true) {
			biocondition=false;
		}
		else {
			int pom;
			if (komestapripada.size() == 1)
				pom = 0;
			else
				pom = komestapripada.get(komestapripada.size() - 2);	
			for(int i=poslednjalista.size()-1;i>=pom;i--) {
				poslednjalista.remove(i);
			}
			komestapripada.remove(komestapripada.size()-1);
		}
		for (int i = 0; i < pcuslovi.size(); i++) {
			Code.fixup(pcuslovi.get(i));
			poslednjalista.add(pcuslovi.get(i));
		}
		poslednjipcuslovi.add(pcuslovi.get(pcuslovi.size() - 1));
		komestapripada.add(poslednjalista.size());
		pcuslovi.clear();
	}

	public ArrayList<Boolean> poceodowhile = new ArrayList<Boolean>();
	public ArrayList<Integer> poceodowhilepc = new ArrayList<Integer>();
	public ArrayList<Integer> breakpcfix = new ArrayList<Integer>();
	public ArrayList<Integer> breakpcnumber = new ArrayList<Integer>();

	public void visit(PocDoWh pdw) {
		poceodowhile.add(true);
		poceodowhilepc.add(Code.pc);
		breakpcnumber.add(0);
		//System.out.println("TRUE");
	}

	public void visit(Smenasaifom ssif) {
		poceodowhile.add(false);
		//System.out.println("FALSE");
	}

	public void visit(StatementBreak sb) {
		breakpcfix.add(Code.pc + 1);
		breakpcnumber.set(breakpcnumber.size() - 1, breakpcnumber.get(breakpcnumber.size() - 1) + 1);
		Code.putJump(0);
	}

	public void visit(StatementContinue sc) {
		Code.putJump(poceodowhilepc.get(poceodowhilepc.size() - 1));
	}

	public void visit(Condition con) {
		biocondition=true;
		for (int i = 0; i < poslednjipcuslovi.size(); i++) {
			//System.out.println("PRINT IZ CONDITIONA");
			switch (Code.buf[poslednjipcuslovi.get(i) - 1] - 43) {
			case EQ:
				//System.out.println("EQ "+Code.buf[poslednjipcuslovi.get(i) - 1]);
				Code.buf[poslednjipcuslovi.get(i) - 1] = (byte) (Code.ne + 43);
				break;
			case NEQ:
				//System.out.println("NEQ "+Code.buf[poslednjipcuslovi.get(i) - 1]);
				Code.buf[poslednjipcuslovi.get(i) - 1] = (byte) (Code.eq + 43);
				break;
			case G:
				//System.out.println(Code.buf[poslednjipcuslovi.get(i) - 1]);
				Code.buf[poslednjipcuslovi.get(i) - 1] = (byte) (Code.le + 43);
				break;
			case GE:
				//System.out.println(Code.buf[poslednjipcuslovi.get(i) - 1] - 43);
				Code.buf[poslednjipcuslovi.get(i) - 1] = (byte) (Code.lt + 43);
				break;
			case L:
				//System.out.println(Code.buf[poslednjipcuslovi.get(i) - 1]);
				Code.buf[poslednjipcuslovi.get(i) - 1] = (byte) (Code.ge + 43);
				break;
			case LE:
				//System.out.println(Code.buf[poslednjipcuslovi.get(i) - 1]);
				Code.buf[poslednjipcuslovi.get(i) - 1] = (byte) (Code.gt + 43);
				break;
			default:
				// System.out.println(Code.buf[poslednjipcuslovi.get(i) - 1]);
				break;
			}
			if (poceodowhile.get(poceodowhile.size() - 1) == false) {
				//System.out.println("Poceo DO WHILE je FALSE");
				Code.fixup(poslednjipcuslovi.get(i));
			} else {
				//System.out.println(
					//	"Poceo DO WHILE je TRUE pc koji se gura je " + poceodowhilepc.get(poceodowhilepc.size() - 1));
				Code.put2(poslednjipcuslovi.get(i),
						(poceodowhilepc.get(poceodowhilepc.size() - 1) - poslednjipcuslovi.get(i) + 1));
			}
		}
		poslednjipcuslovi.clear();
	}


	public ArrayList<Integer> preskocijump=new ArrayList<Integer>();

	public void visit(StatementIf si) {
		if (preskocijump.size() != 0) {
			Code.fixup(preskocijump.get(preskocijump.size()-1));
			preskocijump.remove(preskocijump.size()-1);
		}
	}

	public void visit(StatementDoWhile sdw) {
		//System.out.println("USAO U StatementDoWhile");
		int pom;
		if (komestapripada.size() == 1)
			pom = 0;
		else
			pom = komestapripada.get(komestapripada.size() - 2);
		poceodowhile.remove(poceodowhile.size() - 1);
		poceodowhilepc.remove(poceodowhilepc.size() - 1);
		for (int i = breakpcnumber.get(breakpcnumber.size() - 1); i > 0; i--) {
			Code.fixup(breakpcfix.get(breakpcfix.size() - 1));
			breakpcfix.remove(breakpcfix.size() - 1);
		}
		breakpcnumber.remove(breakpcnumber.size() - 1);
		//System.out.println("SKIDA SE POSLEDNJI ELEMENT");
		for (int i = komestapripada.get(komestapripada.size() - 1) - 1; i >= pom; i--) {
			poslednjalista.remove(i);
		}
		komestapripada.remove(komestapripada.size() - 1);
	}

	public void visit(ElseSmena es) {
		preskocijump.add(Code.pc + 1);
		Code.putJump(0);
		//System.out.println("USAO U ElseSmenu");
		int pom;
		if (komestapripada.size() == 1)
			pom = 0;
		else
			pom = komestapripada.get(komestapripada.size() - 2);
		for (int i = pom; i < komestapripada.get(komestapripada.size() - 1); i++) {
			if (i == (komestapripada.get(komestapripada.size() - 1) - 1)) {
				//System.out.println("USAO U ElseSmenu if");
				switch (Code.buf[poslednjalista.get(i) - 1] - 43) {
				case EQ:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.ne + 43);
					break;
				case NEQ:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.eq + 43);
					break;
				case G:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.le + 43);
					break;
				case GE:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.lt + 43);
					break;
				case L:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.ge + 43);
					break;
				case LE:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.gt + 43);
					break;
				default:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					break;
				}
			}
			Code.fixup(poslednjalista.get(i));
		}
		//System.out.println("Pripadaju mi " + (komestapripada.get(komestapripada.size() - 1) - pom) + " elemenata");
		for (int i = komestapripada.get(komestapripada.size() - 1) - 1; i >= pom; i--) {
			poslednjalista.remove(i);
		}
		komestapripada.remove(komestapripada.size() - 1);
		//System.out.println("SKIDA SE POSLEDNJI ELEMENT");
		poceodowhile.remove(poceodowhile.size() - 1);
	}

	public void visit(NotMaybeElseStatement si) {
		//System.out.println("USAO U NOTMAYBEELSE");
		preskocijump.add(Code.pc + 1);
		Code.putJump(0);
		int pom;
		if (komestapripada.size() == 1)
			pom = 0;
		else
			pom = komestapripada.get(komestapripada.size() - 2);
		for (int i = pom; i < komestapripada.get(komestapripada.size() - 1); i++) {
			if (i == komestapripada.get(komestapripada.size() - 1) - 1) {
				//System.out.println("USAO U NOTMAYBEELSE if");
				switch (Code.buf[poslednjalista.get(i) - 1] - 43) {
				case EQ:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.ne + 43);
					break;
				case NEQ:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.eq + 43);
					break;
				case G:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.le + 43);
					break;
				case GE:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.lt + 43);
					break;
				case L:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.ge + 43);
					break;
				case LE:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					Code.buf[poslednjalista.get(i) - 1] = (byte) (Code.gt + 43);
					break;
				default:
					//System.out.println(Code.buf[poslednjalista.get(i) - 1]);
					break;
				}
			} 
				Code.fixup(poslednjalista.get(i));
		}
		for (int i = komestapripada.get(komestapripada.size() - 1) - 1; i >= pom; i--) {
			poslednjalista.remove(i);
		}
		komestapripada.remove(komestapripada.size() - 1);
		//System.out.println("SKIDA SE POSLEDNJI ELEMENT");
		poceodowhile.remove(poceodowhile.size() - 1);
	}

	public void visit(NotMaybeRE nmre) {
		//System.out.println("GURA ZAPAMCENI OPERATOR NA STEK");
		Code.loadConst(0);
		pcuslovi.add(Code.pc + 1);
		Code.putFalseJump(Code.ne, 0);

	}

	public void visit(XMaybeRelopExpr xmre) {
		// treba gurnuti zapamceni relacioni operator
		//System.out.println("GURA ZAPAMCENI OPERATOR NA STEK");
		pcuslovi.add(Code.pc + 1);
		switch (relopoperacija) {
		case EQ:
			Code.putFalseJump(Code.eq, 0);
			break;
		case NEQ:
			Code.putFalseJump(Code.ne, 0);
			break;
		case G:
			Code.putFalseJump(Code.gt, 0);
			break;
		case GE:
			Code.putFalseJump(Code.ge, 0);
			break;
		case L:
			Code.putFalseJump(Code.lt, 0);
			break;
		case LE:
			Code.putFalseJump(Code.le, 0);
			break;
		}
	}

	public void visit(Eq eq) {
		relopoperacija = EQ;
	}

	public void visit(Neq neq) {
		relopoperacija = NEQ;
	}

	public void visit(G g) {
		relopoperacija = G;
	}

	public void visit(Ge ge) {
		relopoperacija = GE;
	}

	public void visit(L l) {
		relopoperacija = L;
	}

	public void visit(Le le) {
		relopoperacija = LE;
	}

}
