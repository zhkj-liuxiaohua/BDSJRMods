package team.mzgl.mods.unlockjse;

import BDS.MCJAVAAPI;

public class UnlockJSE {

	public static void init(MCJAVAAPI api) {
		api.log("[UlockJSE] 脚本强开已加载。请等待版本适配...");
		boolean unlocked = false;
		switch(api.getVersion()) {
		case "1.16.201.3":
		{
			byte[] jmp_explaycheckcode = { (byte)0xeb, 0x07, 0, 0, 0, 0, 0, 0, 0 };	// IDA jmp short + 7, hex data
			unlocked = api.writeHardMemory(0x0CB1379, jmp_explaycheckcode, 9);		// IDA MinecraftServerScriptEngine::onServerThreadStarted + 0x69
		}
			break;
		}
		if (unlocked) {
			api.log("[UlockJSE] 脚本引擎已强开。");
		}
	}
}
