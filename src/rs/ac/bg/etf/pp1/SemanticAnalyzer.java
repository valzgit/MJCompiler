package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemanticAnalyzer extends VisitorAdaptor {
	int printCallCount = 0;
	int varDeclCount = 0;
	int formDeclCount = 0;
	int classDeclCount = 0;
	Obj currentMethod = null;
	boolean returnFound = false;
	boolean errorDetected = false;
	Struct zapamtistruct = null;
	int constDeclCount = 0;
	int nVars = 0;
	int indeksiranjeniza = 0;
	int currentlevel = 0;
	boolean uglastekreiranje = false;
	boolean biominus = false;
	boolean bioputa = false;
	boolean bionew = false;

	Logger log = Logger.getLogger(getClass());

	/////////////////////////////////// UGRADJENJE FUNKCIJE ZA
	/////////////////////////////////// ISPIS/////////////////////////////////////////////

	public boolean passed() {
		return !errorDetected;
	}

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append(" na liniji ").append(line);
		log.info(msg.toString());
	}

///////////////////////////////////UGRADJENJE FUNKCIJE ZA ISPIS/////////////////////////////////////////////

//////////////////////////////////////////////////PROGRAM VISIT//////////////////////////////////////////
	public void visit(Program program) {
		nVars = Tabela.currentScope.getnVars();
		Tabela.chainLocalSymbols(program.getProgName().obj);
		Tabela.closeScope();
	}

	public void visit(ProgName pn) {
		pn.obj = Tabela.insert(Obj.Prog, pn.getProgName(), Tabela.noType);
		Tabela.openScope();
	}

	////////////////////////////////////////////////// PROGRAM
	////////////////////////////////////////////////// VISIT//////////////////////////////////////////

////////////////////////////////////////////////////VARCONSTCLASS/////////////////////////////////

	public void visit(FactorNumConst fnc) {
		fnc.struct = Tabela.intType;
	}

	public void visit(FactorBooleanConst fbc) {
		fbc.struct = Tabela.boolType;
	}

	public void visit(FactorCharConst fcc) {
		fcc.struct = Tabela.charType;
	}

	public void visit(Tipovi tt) {
		zapamtistruct = tt.getType().struct;
	}

	public void visit(Uglaste u) {
		uglastekreiranje = true;
	}

	public void visit(PrvakFormA fp) {
		if (Tabela.find(fp.getFormpars()) != Tabela.noObj && currentlevel == Tabela.find(fp.getFormpars()).getLevel()) {
			report_error("PrvakFormA DVA PUTA DEKLARISANA PROMENLJIVA " + fp.getFormpars(), fp);
			return;
		}
		formDeclCount++;
		report_info("Deklarisana promenljiva " + fp.getFormpars(), fp);
		Struct s;
		if (uglastekreiranje == false) {
			s = zapamtistruct;
		} else {
			if (zapamtistruct.equals(Tabela.intType)) {
				s = Tabela.arrTypeInt;
			} else if (zapamtistruct.equals(Tabela.charType)) {
				s = Tabela.arrTypeChar;
			} else {
				s = Tabela.arrTypeBool;
			}
			s.setElementType(zapamtistruct);
		}

		Tabela.insert(Obj.Var, fp.getFormpars(), s);

		uglastekreiranje = false;
	}

	public void visit(XMaybeFormPars xmfp) {
		if (Tabela.find(xmfp.getMformpars()) != Tabela.noObj
				&& currentlevel == Tabela.find(xmfp.getMformpars()).getLevel()) {
			report_error("XMaybeFormPars DVA PUTA DEKLARISANA PROMENLJIVA " + xmfp.getMformpars(), xmfp);
			return;
		}
		formDeclCount++;
		report_info("Deklarisana promenljiva " + xmfp.getMformpars(), xmfp);
		Struct s;
		if (uglastekreiranje == false) {
			s = zapamtistruct;
		} else {
			if (zapamtistruct.equals(Tabela.intType)) {
				s = Tabela.arrTypeInt;
			} else if (zapamtistruct.equals(Tabela.charType)) {
				s = Tabela.arrTypeChar;
			} else {
				s = Tabela.arrTypeBool;
			}
			s.setElementType(zapamtistruct);
		}

		Tabela.insert(Obj.Var, xmfp.getMformpars(), s);

		uglastekreiranje = false;
	}

	public void visit(XVarDecl vardecl) {
//		varDeclCount++;
//		report_info("Deklarisana promenljiva " + vardecl.getVarName(), vardecl);
//		Struct s;
//		if (uglastekreiranje == false) {
//			s = zapamtistruct;
//		} else {
//			if (zapamtistruct.equals(Tabela.intType)) {
//				s = Tabela.arrTypeInt;
//			} else if (zapamtistruct.equals(Tabela.charType)) {
//				s = Tabela.arrTypeChar;
//			} else {
//				s = Tabela.arrTypeBool;
//			}
//			s.setElementType(zapamtistruct);
//		}
//		
//		Tabela.insert(Obj.Var, vardecl.getVarName(), s);
//
//		uglastekreiranje = false;
	}

	public void visit(DeklaracijaPrvog vardecl) {
		if (Tabela.find(vardecl.getI1()) != Tabela.noObj && currentlevel == Tabela.find(vardecl.getI1()).getLevel()) {
			report_error("DeklaracijaPrvog DVA PUTA DEKLARISANA PROMENLJIVA " + vardecl.getI1(), vardecl);
			return;
		}
		varDeclCount++;
		report_info("Deklarisana promenljiva " + vardecl.getI1(), vardecl);
		Struct s;
		if (uglastekreiranje == false) {
			s = zapamtistruct;
		} else {
			if (zapamtistruct.equals(Tabela.intType)) {
				s = Tabela.arrTypeInt;
			} else if (zapamtistruct.equals(Tabela.charType)) {
				s = Tabela.arrTypeChar;
			} else {
				s = Tabela.arrTypeBool;
			}
			s.setElementType(zapamtistruct);
		}

		Tabela.insert(Obj.Var, vardecl.getI1(), s);

		uglastekreiranje = false;
	}

	public void visit(MoreVars mv) {
		if (Tabela.find(mv.getMvl()) != Tabela.noObj && currentlevel == Tabela.find(mv.getMvl()).getLevel()) {
			report_error("MoreVars DVA PUTA DEKLARISANA PROMENLJIVA " + mv.getMvl(), mv);
			return;
		}
		varDeclCount++;
		// tip deteta mozda nije dobar
		report_info("Deklarisana promenljiva " + mv.getMvl(), mv);
		Struct s;
		if (uglastekreiranje == false) {
			s = zapamtistruct;
		} else {
			if (zapamtistruct.equals(Tabela.intType)) {
				s = Tabela.arrTypeInt;
			} else if (zapamtistruct.equals(Tabela.charType)) {
				s = Tabela.arrTypeChar;
			} else {
				s = Tabela.arrTypeBool;
			}
			s.setElementType(zapamtistruct);
		}

		Tabela.insert(Obj.Var, mv.getMvl(), s);

		uglastekreiranje = false;
	}

	public void visit(ConstDecl cd) {
		if (Tabela.find(cd.getCdecl()) != Tabela.noObj && currentlevel == Tabela.find(cd.getCdecl()).getLevel()) {
			report_error("ConstDecl DVA PUTA DEKLARISANA PROMENLJIVA " + cd.getCdecl(), cd);
			return;
		}
		constDeclCount++;
		report_info("Deklarisana konstanta " + cd.getCdecl(), cd);
		Obj constNode = Tabela.insert(Obj.Con, cd.getCdecl(), zapamtistruct);
		if (zapamtistruct == Tabela.boolType) {
			constNode.setAdr(zapamtiboolprvi);
		} else if (zapamtistruct == Tabela.charType) {
			constNode.setAdr(zapamticharprvi);
		} else {
			constNode.setAdr(zapamtiintprvi);
		}
	}

	public void visit(MoreConsts mc) {
		if (Tabela.find(mc.getMcl()) != Tabela.noObj && currentlevel == Tabela.find(mc.getMcl()).getLevel()) {
			report_error("MoreConsts DVA PUTA DEKLARISANA PROMENLJIVA " + mc.getMcl(), mc);
			return;
		}
		constDeclCount++;
		// tip deteta mozda nije dobar
		report_info("Deklarisana konstanta " + mc.getMcl(), mc);
		Obj constNode = Tabela.insert(Obj.Con, mc.getMcl(), zapamtistruct);
		if (zapamtistruct == Tabela.boolType) {
			constNode.setAdr(zapamtibool);
		} else if (zapamtistruct == Tabela.charType) {
			constNode.setAdr(zapamtichar);
		} else {
			constNode.setAdr(zapamtiint);
		}
	}

	public int zapamtiintprvi;
	public int zapamtiboolprvi;
	public char zapamticharprvi;

	public void visit(MaybeNumPrvi mn) {
		zapamtiintprvi = mn.getNprvi();
	}

	public void visit(MaybeCharPrvi mc) {
		zapamticharprvi = mc.getCharvrednostprvi().charAt(1);
	}

	public void visit(MaybeBooleanPrvi mb) {
		if (mb.getVrednostbprvi().equals("true")) {
			zapamtiboolprvi = 1;
		} else {
			zapamtiboolprvi = 0;
		}
	}

	public int zapamtiint;
	public int zapamtibool;
	public char zapamtichar;

	public void visit(MaybeNum mn) {
		zapamtiint = mn.getN();
	}

	public void visit(MaybeChar mc) {
		zapamtichar = mc.getCharvrednost().charAt(1);
	}

	public void visit(MaybeBoolean mb) {
		if (mb.getVrednostb().equals("true")) {
			zapamtibool = 1;
		} else {
			zapamtibool = 0;
		}
	}

	//////////////////////////////////////////////////// VARCONSTCLASS/////////////////////////////////
	public void visit(StatementReturn sr) {
		if (((currentMethod != null) && (sr.getMaybeExpr().struct == null) && (currentMethod.getType() == Tabela.noType))
				|| ((sr.getMaybeExpr() != null) && (sr.getMaybeExpr().struct == currentMethod.getType()))) {
			report_info("Ispravna return naredba! " + "koja se nalazi na ", sr);
			returnFound = true;
		} else {
			report_error("Neispravna RETURN naredba! ", sr);
			returnFound = false;
		}
	}

	public void visit(Type type) {
		Obj typeNode = Tabela.find(type.getTypeName());
		if (typeNode == Tabela.noObj) {
			report_error("Nije pronadjen tip " + type.getTypeName() + " u tabeli simbola!", null);
			type.struct = Tabela.noType;
		} else {
			if (Obj.Type == typeNode.getKind()) {
				type.struct = typeNode.getType();
			} else {
				report_error("Greska : Ime " + type.getTypeName() + " ne predstavlja tip!", type);
				type.struct = Tabela.noType;
			}
		}
	}

	public void visit(MethodTypeName methodTypeName) {
		currentMethod = Tabela.insert(Obj.Meth, methodTypeName.getMdname(), methodTypeName.getType().struct);
		methodTypeName.obj = currentMethod;
		Tabela.openScope();
		currentlevel++;
		report_info("Obradjuje se funkcija " + methodTypeName.getMdname(), methodTypeName);
	}

	public void visit(MethodVoidName methodVoidName) {
		currentMethod = Tabela.insert(Obj.Meth, methodVoidName.getMdvoid(), Tabela.noType);
		methodVoidName.obj = currentMethod;
		Tabela.openScope();
		currentlevel++;
		report_info("Obradjuje se funkcija " + methodVoidName.getMdvoid(), methodVoidName);
	}

	public void visit(MethodDeclType methodDecl) {
		if (!returnFound && currentMethod.getType() != Tabela.noType) {
			report_error("Semanticka greska na liniji " + methodDecl.getLine() + ": funckija " + currentMethod.getName()
					+ " nema return iskaz!", null);
		}
		currentMethod.setLevel(formDeclCount);
		formDeclCount = 0;
		Tabela.chainLocalSymbols(currentMethod);
		Tabela.closeScope();
		currentlevel--;
		returnFound = false;
		currentMethod = null;
	}

	public void visit(MethodDeclVoid methodDeclVoid) {
		currentMethod.setLevel(formDeclCount);
		formDeclCount = 0;
		Tabela.chainLocalSymbols(currentMethod);
		Tabela.closeScope();
		currentlevel--;
		currentMethod = null;
		returnFound = false;
	}

	public void visit(DesigIdent designator) {
		Obj obj = Tabela.find(designator.getDesignatorName());
		if (obj == Tabela.noObj) {
			report_error("Greska na liniji " + designator.getLine() + " : ime " + designator.getDesignatorName()
					+ " nije deklarisano! ", null);
		}
		designator.obj = obj;
	}

	public void visit(Designator designator) {
		// DODATO ZA B DEO
		if ((designator.getDesignatorIdent().obj.getKind() == Obj.Meth)
				&& designator.getDesignatorIdent().obj.getLevel() != 0) {
			pozivfunkcije.add(designator.getDesignatorIdent().obj);
			paramfunkcije.add(0);
		}
		// DODATO ZA B DEO
		if (indeksiranjeniza > 0 && (designator.getDesignatorIdent().obj.getType() == Tabela.arrTypeInt
				|| designator.getDesignatorIdent().obj.getType() == Tabela.arrTypeChar
				|| designator.getDesignatorIdent().obj.getType() == Tabela.arrTypeBool)) {
			designator.obj = new Obj(designator.getDesignatorIdent().obj.getKind(), "nebitan",
					designator.getDesignatorIdent().obj.getType().getElemType());
			indeksiranjeniza--;
		} else {
			designator.obj = designator.getDesignatorIdent().obj;
		}

	}

	public void visit(FactorDesignator fd) {
		fd.struct = fd.getDesignator().obj.getType();
	}

	public void visit(FactorExpr fe) {
		fe.struct = fe.getExpr().struct;
	}

	////////////////////////////////////////////////////////// DODELE////NIZOVI///////////////////////////////////////////////////
	public void visit(FactorNEW fnew) {
		if (fnew.getType().struct.equals(Tabela.intType)) {
			fnew.struct = Tabela.arrTypeInt;
		} else if (fnew.getType().struct.equals(Tabela.charType)) {
			fnew.struct = Tabela.arrTypeChar;
		} else {
			fnew.struct = Tabela.arrTypeBool;
		}
		bionew = true;

		fnew.struct.setElementType(fnew.getType().struct);
	}

	public void visit(XMaybeUgExpr xmye) {
		if (xmye.getExpr().struct != Tabela.intType) {
			report_error("Kreiranje novog tipa ne sme da koristi tip razlicit od integer! : linija " + xmye.getLine(),
					null);
		}
	}

	public void visit(Term term) {
		Struct s = term.getFactor().struct;
		if (bionew == true) {
			term.struct = s;
			bionew = false;
			return;
		}
		if (bioputa == false || (s == Tabela.intType && bioputa == true)) {
			term.struct = s;
		} else {
			report_error("Postoji mnozenje neadekvatnih tipova (" + term.getFactor().struct.getKind() + ") na liniji :"
					+ term.getLine(), null);
			term.struct = Tabela.noType;
		}

		bioputa = false;
	}

	public void visit(XMaybeMulopFactor xmmf) {
		bioputa = true;
	}

	public void visit(MulopFactor mf) {
		Struct s = mf.getFactor().struct;
		if (s != Tabela.intType) {
			report_error("Nisu svi elementi koji se mnoze tipa int ili niz od int : tip je "
					+ mf.getFactor().struct.getKind(), null);
		}
	}

	public void visit(UslovTernarni ut) {
		ut.struct = ut.getExpr1().struct;
	}

	public void visit(PrviTernarni ut) {
		ut.struct = ut.getExpr1().struct;
	}

	public void visit(DrugiTernarni ut) {
		ut.struct = ut.getExpr1().struct;
	}

	// EXPRZIZA2 NEMA SMSLA DA POSTOJI SEM DA PREPISE OD DONJEG TIP
	public void visit(ExprZiza2 ez2) {
		if (biominus == false || (biominus = true && ez2.getExpr1().struct == Tabela.intType))
			ez2.struct = ez2.getExpr1().struct;
		else {
			report_error("Greska na liniji " + ez2.getLine() + " : nekompatibilni tipovi su negativni.", null);
			ez2.struct = Tabela.noType;
		}
		biominus = false;
	}

	public void visit(ExprZiza1 ez1) {
		if (ez1.getPrviTernarni().struct.equals(ez1.getDrugiTernarni().struct)) {
			ez1.struct = ez1.getPrviTernarni().struct;
		} else {
			ez1.struct = Tabela.noType;
			report_error("Greska na liniji " + ez1.getLine() + " : izrazi nisu istog tipa", null);
		}
	}

	public void visit(XMaybeSub xms) {
		biominus = true;
	}

	public void visit(Expr1 e1) {
		Struct te = e1.getMaybeAddopTerms().struct;
		Struct t = e1.getTerm().struct;
		if ((te == null || te.equals(t)) && (biominus == false || (biominus == true && t.equals(Tabela.intType)))) {
			e1.struct = t;
		} else {
			report_error("Greska na liniji " + e1.getLine() + " : nekompatibilni tipovi za dodelu.", null);
			e1.struct = Tabela.noType;

		}

		biominus = false;
	}

	public void visit(XMaybeAddopTerms xmat) {
		Struct te = xmat.getMaybeAddopTerms().struct;
		Struct t = xmat.getTerm().struct;

		if (t.equals(Tabela.intType) && (te == null || (te.equals(t)))) {
			xmat.struct = t;
		} else {
			report_error("Greska na liniji " + xmat.getLine() + " : nekompatibilni tipovi u izrazu za sabiranje.",
					null);
			xmat.struct = Tabela.noType;

		}

	}

	public void visit(XMaybeExpr xme) {
		xme.struct = xme.getExpr().struct;
	}

	public void visit(StatementRead sread) {
		if (sread.getDesignator().obj.getKind() == Obj.Var) {
			Struct s = sread.getDesignator().obj.getType();
			if (s == Tabela.intType || s == Tabela.charType || s == Tabela.boolType)
				report_info("Adekvatan READ na liniji: " + sread.getLine(), null);
			else {
				report_error("Neadekvatna READ komanda", sread);
			}
		} else {
			report_error("Zabranjeno je ucitavati u element koji nije varijabla!", sread);
		}

	}

	public void visit(StatementPrint sprint) {
		Struct s = sprint.getExpr().struct;
		if (s == Tabela.intType || s == Tabela.charType || s == Tabela.boolType) {
			// report_info("Adekvatan PRINT na liniji: " + sprint.getLine(), null);
		} else {
			report_error("Neadekvatna PRINT komanda", sprint);
		}

	}

	public void visit(DesStatINC dsi) {
		Struct s = dsi.getDesignator().obj.getType();
		if (!s.equals(Tabela.arrTypeBool) && !s.equals(Tabela.arrTypeChar) && !s.equals(Tabela.arrTypeInt)) {
			report_info(
					"Inkrementiranje promenljive " + dsi.getDesignator().obj.getName() + " na liniji " + dsi.getLine(),
					null);
		} else {
			report_error("Neuspesno inkrementiranje promenljive " + dsi.getDesignator().obj.getName() + " na liniji "
					+ dsi.getLine(), null);

		}

	}

	public void visit(DesStatDEC dsd) {
		Struct s = dsd.getDesignator().obj.getType();
		if (!s.equals(Tabela.arrTypeBool) && !s.equals(Tabela.arrTypeChar) && !s.equals(Tabela.arrTypeInt)) {
			report_info(
					"Dekrementiranje promenljive " + dsd.getDesignator().obj.getName() + " na liniji " + dsd.getLine(),
					null);
		} else {
			report_error("Neuspesno inkrementiranje promenljive " + dsd.getDesignator().obj.getName() + " na liniji "
					+ dsd.getLine(), null);
		}

	}

	public void visit(DesStatAssign dsa) {
		if (dsa.getExpr().struct.assignableTo(dsa.getDesignator().obj.getType())) {
			report_info("Dodela promenljivoj " + dsa.getDesignator().obj.getName() + " na liniji " + dsa.getLine(),
					null);
		} else {
			report_error("Greska na liniji " + dsa.getLine() + " : " + "nekompatibilni tipovi u dodeli vrednosti!",
					null);
			report_error(dsa.getExpr().struct.getKind() + " " + dsa.getDesignator().obj.getName() + " "
					+ dsa.getDesignator().obj.getType().getKind(), null);
		}

	}

	public void visit(XDesignatorList2 xdl2) {
		if (!(xdl2.getExpr().struct == Tabela.intType)) {
			report_error("Nije dozvoljeno indeksiranje niza necim sto nije broj: na liniji " + xdl2.getLine(), null);
		}

		indeksiranjeniza++;
	}

	/////////////////////////////////////////////////////////////// FAZA
	/////////////////////////////////////////////////////////////// B////////////////////////////
	public ArrayList<Obj> pozivfunkcije = new ArrayList<Obj>();
	public ArrayList<Integer> paramfunkcije = new ArrayList<Integer>();
	public ArrayList<Boolean> mozebreak = new ArrayList<Boolean>();

	public void visit(CondFact cf) {
		Struct te = cf.getMaybeRelopExpr().struct;
		Struct t = cf.getExpr().struct;

		if ((te != null && (t.equals(Tabela.intType) || t.equals(Tabela.charType)) && te.equals(t)) || (te == null && t.equals(Tabela.boolType))) {
			cf.struct = Tabela.boolType;
			// report_info("Uspesan condfact ", cf);
		} else {
			report_error("Nekompatibilni tipovi u CONDFACT izrazu.", cf);
			cf.struct = Tabela.noType;

		}
	}

	public void visit(XMaybeZarezExpr xmze) {
		if (pozivfunkcije.size() != 0) {
			int pom = paramfunkcije.get(paramfunkcije.size() - 1);
			pom++;
			Iterator<Obj> iterator = pozivfunkcije.get(pozivfunkcije.size() - 1).getLocalSymbols().iterator();
			int brojac = 0;
			while (iterator.hasNext()) {
				Obj o = iterator.next();
				if (brojac != pom) {
					brojac++;
					continue;
				} else {
					if (o.getType() != xmze.getExpr().struct) {
						report_error("ACTPARS Pogresan tip izraza je prosledjen kao " + pom + ". parametar funkcije "
								+ pozivfunkcije.get(pozivfunkcije.size() - 1).getName(), xmze);
					}
					break;
				}
			}
			paramfunkcije.set(paramfunkcije.size() - 1, paramfunkcije.get(paramfunkcije.size() - 1) + 1);
		}
	}

	public void visit(ActPars xmap) {
		int pom = 0;
		if (pozivfunkcije.size() != 0) {
			Iterator<Obj> iterator = pozivfunkcije.get(pozivfunkcije.size() - 1).getLocalSymbols().iterator();
			Obj o = iterator.next();
			if (o.getType()!= xmap.getExpr().struct && !(pozivfunkcije.get(pozivfunkcije.size() - 1).getName().equals("len"))) {
				report_error("ACTPARS Pogresan tip izraza je prosledjen kao " + pom + "-ti parametar funkcije "
						+ pozivfunkcije.get(pozivfunkcije.size() - 1).getName(), xmap);
			}
			if (pozivfunkcije.get(pozivfunkcije.size() - 1)
					.getLevel() != (paramfunkcije.get(paramfunkcije.size() - 1) + 1)) {
				report_error("Funkciji " + pozivfunkcije.get(pozivfunkcije.size() - 1).getName()
						+ " je prosledjen pogresan broj parametara TREBA "
						+ pozivfunkcije.get(pozivfunkcije.size() - 1).getLevel() + " PROSLEDJENO "
						+ (paramfunkcije.get(paramfunkcije.size() - 1) + 1), xmap);
			}
			pozivfunkcije.remove(pozivfunkcije.size() - 1);
			paramfunkcije.remove(paramfunkcije.size() - 1);
		}
	}

	public void visit(PocDoWh pdw) {
		mozebreak.add(true);
	}

	public void visit(StatementDoWhile sdw) {
		mozebreak.remove(mozebreak.size() - 1);
	}

	public void visit(StatementBreak sb) {
		if (mozebreak.size() == 0) {
			report_error("BREAK stoji van DO WHILE petlje " + sb.getLine(), null);
		}
	}

	public void visit(StatementContinue sc) {
		if (mozebreak.size() == 0) {
			report_error("CONTINUE stoji van DO WHILE petlje " + sc.getLine(), null);
		}
	}

	public void visit(XMaybeRelopExpr xmre) {
		xmre.struct = xmre.getExpr().struct;
	}

}
