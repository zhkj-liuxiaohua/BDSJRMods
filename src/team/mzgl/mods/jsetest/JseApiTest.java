package team.mzgl.mods.jsetest;

import com.alibaba.fastjson.*;

import BDS.MCJAVAAPI;

public class JseApiTest {

	static MCJAVAAPI mapi;
	
	static void loadAddjs() {
		String js = "var system = server.registerSystem(0, 0);\n" +
				"let logs = system.createEventData('minecraft:script_logger_config');\n" +
		        "logs.data.log_information = true;\n" +
		        "logs.data.log_errors = true;\n" +
		        "logs.data.log_warnings = true;\n" +
		        "system.broadcastEvent('minecraft:script_logger_config', logs);\n" +
				"system.listenForEvent('mytest:testevent', (e)=>{console.log('我将输出' + e.data);});\n" +
				"system.listenForEvent('CSRCall:runscript', (e)=>{console.log('' + eval(e.data));});\n" +
				"system.listenForEvent('CSRCall:runcmd', (e)=>{system.executeCommand(e.data, (ccb)=>{console.log(ccb);});});\n";
			mapi.log("脚本内容=" + js);
			mapi.JSErunScript(js, new MCJAVAAPI.JSECab() {
				@Override
				public void callback(boolean r) {
					mapi.log("执行结果=" + r);
				}
			});
	}
	
	public static void init(MCJAVAAPI api) {
		mapi = api;
		// 引擎初始化监听
		api.addAfterActListener("onScriptEngineInit", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				api.log("[JA] 官方脚本引擎已启动，即将开启联动任务");
				// 延时3秒，载入addon js脚本
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						loadAddjs();
					}
				}).start();
				// 延时4秒，发送log消息测试
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(4000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						api.JSEfireCustomEvent("mytest:testevent", "一个文本", new MCJAVAAPI.JSECab() {
							@Override
							public void callback(boolean r) {
								if (r) {
									api.log("自定义事件信息发送成功。");
								}
							}
						});
					}
				}).start();
				// 延时5秒，发送临时脚本测试
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						api.JSEfireCustomEvent("CSRCall:runscript", "system.executeCommand(\"/me 123\", null);", new MCJAVAAPI.JSECab() {
							@Override
							public void callback(boolean r) {
								if (r) {
									api.log("自定义临时脚本发送成功。");
								}
							}
						});
					}
				}).start();
				// 延时6秒，发送引擎指令测试
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						api.JSEfireCustomEvent("CSRCall:runcmd", "/tpa x", new MCJAVAAPI.JSECab() {
							@Override
							public void callback(boolean r) {
								if (r)
									api.log("自定义指令发送成功。");
							}
						});
					}
				}).start();
				return false;
			}
		});
		// 引擎接收日志监听
		api.addAfterActListener("onScriptEngineLog", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				api.log("[JA] 来自官方脚本引擎的LOG，内容=" + je.getString("log"));
				return false;
			}
		});
		// 引擎执行指令监听
		api.addAfterActListener("onScriptEngineCmd", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				String cmd = je.getString("cmd");
				api.log("[JA] 来自官方脚本引擎的指令，内容=" + cmd);
				if (cmd.indexOf("/tpa") == 0) {	// 特定的插件指令处理，拦截
					return false;
			    }
				return true;
			}
		});
	}
}
