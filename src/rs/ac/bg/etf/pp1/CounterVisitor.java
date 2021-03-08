package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.FormPars;
import rs.ac.bg.etf.pp1.ast.MoreVars;
import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;
import rs.ac.bg.etf.pp1.ast.XMaybeFormPars;
import rs.ac.bg.etf.pp1.ast.XVarDecl;

public class CounterVisitor extends VisitorAdaptor {

	protected int count;
	
	public int getCount() {
		return count;
	}
	
	public static class FormParamCounter extends CounterVisitor{
		
		public void visit(FormPars fp) {
			count++;
		}
		public void visit(XMaybeFormPars xmfp) {
			count++;
		}
	}
	
	public static class VarCounter extends CounterVisitor{
		
		public void visit(XVarDecl vd) {
			count++;
		}
		
		public void visit(MoreVars mv) {
			count++;
		}
	}
}
