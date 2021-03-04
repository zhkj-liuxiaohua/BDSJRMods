package team.mzgl.mods.testcase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.alibaba.fastjson.*;

import BDS.MCJAVAAPI;

public class Testcase {
	static MCJAVAAPI mapi;
	static HashMap<String, String> nameuuids = new HashMap<String, String>();
	
	// 获取uuid
	public static String getUUID(String name) {
		String uuid = nameuuids.get(name);
		if (uuid != null && uuid != "") {
			return uuid;
		}
		// 重载在线列表
		nameuuids.clear();
		String ols = mapi.getOnLinePlayers();
		if (ols != null && ols != "") {
			JSONArray ol = JSON.parseArray(ols);
			for (int i = 0, l = ol.size(); i < l; i++) {
				JSONObject p = (JSONObject) ol.get(i);
				String pname = p.getString("playername");
				String puuid = p.getString("uuid");
				nameuuids.put(pname, puuid);
				if (pname.equals(name)) {
					uuid = puuid;
				}
			}
		}
		return uuid;
	}
	
	public static void init(MCJAVAAPI api) {
		mapi = api;
		String [] bts = null;
		bts = new String[] {
				"后台指令tell",
				"前缀/去前缀名",
				"模拟喊话",
				"模拟执行me指令",
				"后台指令输出hello",
				"查询当前状态至后台",
				"给32个白桦木",
				"断开自身连接",
				"发送一条原始文本至玩家聊天框",
				"获取玩家计分板项money的值",
				"设置玩家计分板项money的值+1"	
		};
		ArrayList<String> a = new ArrayList<String>();
		a.addAll(Arrays.asList(bts));
		api.addBeforeActListener("onInputCommand", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject jo = JSON.parseObject(data);
				String cmd = jo.getString("cmd");
				if (cmd.trim().equals("/testcase")) {
					// 此处执行拦截操作
					String uuid = getUUID(jo.getString("playername"));
					SimpleGUI gui = new SimpleGUI(api, uuid, "测试用例", "请于30秒内选择测试用例进行测试：",
						                a);
					gui.send(30000, new SimpleGUI.ONSELECT() {
						@Override
						public void onSelected(String selected) {
							switch(selected) {
							case "0":
								api.runcmd("tell \"" + jo.getString("playername") + "\" 你好 Java !");
								break;
							case "1":
							{
								String r = jo.getString("playername");
								r = (r.indexOf("[前缀]") >= 0 ? r.substring(4) : "[前缀]" + r);
								api.reNameByUuid(uuid, r);
							}
								break;
							case "2":
								api.talkAs(uuid, "你好，Java !");
								break;
							case "3":
								api.runcmdAs(uuid, "/me 你好 Java !");
								break;
							case "4":
								api.logout("logout: 你好 Java !");
								break;
							case "5":
								api.log(api.selectPlayer(uuid));
								break;
							case "6":
								api.addPlayerItem(uuid, (int)17, (short)2, (byte)32);
								break;
							case "7":
								api.disconnectClient(uuid, "这个消息来自测试");
								break;
							case "8":
								api.sendText(uuid, "这个文本来自测试");
								break;
							case "9":
								api.log(String.format("玩家%s计分板money项数值为：%d", jo.getString("playername"),
										api.getscoreboard(uuid, "money")));
								break;
							case "10":
								if (api.setscoreboard(uuid, "money", api.getscoreboard(uuid, "money") + 1))
									api.log("[setscoreboard] 已成功设置计分板项。");
								break;
						}
						}
					}, new SimpleGUI.ONTIMEOUT() {
						@Override
						public void onTimeout() {
							
						}
					});
					return false;
				}
				return true;
			}
		});
		// 离开监听
		api.addAfterActListener("onPlayerLeft", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject jo = JSON.parseObject(data);
				String name = jo.getString("playername");
				String tuuid = nameuuids.get(name);
				if (tuuid != null && tuuid != "") {
					nameuuids.remove(name);
				}
				return true;
			}});
		api.setCommandDescribe("testcase", "开始一个测试用例");
		api.log("[testcase] 测试命令已装载。用法：/testcase");
	}
}
