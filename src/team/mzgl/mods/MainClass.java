package team.mzgl.mods;

import BDS.MCJAVAAPI;
import team.mzgl.mods.example.Example;
import team.mzgl.mods.jsetest.JseApiTest;
import team.mzgl.mods.testcase.Testcase;
import team.mzgl.mods.unlockjse.UnlockJSE;
import team.mzgl.mods.visitor.Visitor;

public class MainClass {
	static MCJAVAAPI api;
	public static void main(String[]args) {
		if (args.length > 1) {
			api = new MCJAVAAPI(args[0], args[1], args[args.length - 1]);
			
			Visitor.init(api);
			//Testcase.init(api);	// api测试
			UnlockJSE.init(api);
			//Example.init(api);	// 事件测试
			//JseApiTest.init(api);	// 脚本引擎测试
		} else {
			System.out.println("This plugin need run on BDSJavaRunner.");
			
		}
	}
}
