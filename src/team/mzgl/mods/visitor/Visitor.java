package team.mzgl.mods.visitor;

import BDS.MCJAVAAPI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.alibaba.fastjson.*;

public class Visitor {

	static MCJAVAAPI mapi;
	
	// 读所有文本
	public static String readToString(String fileName) {
			    String encoding = "UTF-8";
			    File file = new File(fileName);
			    Long filelength = file.length();
			    byte[] filecontent = new byte[filelength.intValue()];
			    try {
			      FileInputStream in = new FileInputStream(file);
			      in.read(filecontent);
			      in.close();
			    } catch (FileNotFoundException e) {
			      e.printStackTrace();
			    } catch (IOException e) {
			      e.printStackTrace();
			    }
			    try {
			      return new String(filecontent, encoding);
			    } catch (UnsupportedEncodingException e) {
			      System.err.println("The OS does not support " + encoding);
			      e.printStackTrace();
			      return null;
			    }
	}
	// 写所有文本
	public static void saveStringTOFile(String filename,String content){
			File f = new File(filename);
			FileWriter writer=null;
			try {
				writer = new FileWriter(f);
				writer.write(content);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	}
	// 判断字符串是否为空
	public static boolean isNullOrEmptyString(String s) {
			return s == null || s == "";
	}
	
	// 获取对应玩家的xuid
	static HashMap<String, String> namexuids = new HashMap<String, String>();
	static HashMap<String, String> nameuuids = new HashMap<String, String>();
	// 从在线列表中获取xuid
	public static String getXUID(String name) {
		String xuid = namexuids.get(name);
		if (xuid != null && xuid != "") {
			return xuid;
		}
		// 重载在线列表
		namexuids.clear();
		nameuuids.clear();
		String ols = mapi.getOnLinePlayers();
		if (!isNullOrEmptyString(ols)) {
			JSONArray ol = JSON.parseArray(ols);
			for (int i = 0, l = ol.size(); i < l; i++) {
				JSONObject p = (JSONObject) ol.get(i);
				String pname = p.getString("playername");
				String pxuid = p.getString("xuid");
				namexuids.put(pname, pxuid);
				if (pname.equals(name)) {
					xuid = pxuid;
				}
				nameuuids.put(pname, p.getString("uuid"));
			}
		}
		return xuid;
	}
	// 从在线列表中获取uuid
		public static String getUUID(String name) {
			String uuid = nameuuids.get(name);
			if (!isNullOrEmptyString(uuid)) {
				return uuid;
			}
			// 重载在线列表
			namexuids.clear();
			nameuuids.clear();
			String ols = mapi.getOnLinePlayers();
			if (!isNullOrEmptyString(ols)) {
				JSONArray ol = JSON.parseArray(ols);
				for (int i = 0, l = ol.size(); i < l; i++) {
					JSONObject p = (JSONObject) ol.get(i);
					String pname = p.getString("playername");
					String puuid = p.getString("uuid");
					nameuuids.put(pname, puuid);
					if (pname.equals(name)) {
						uuid = puuid;
					}
					namexuids.put(pname, p.getString("xuid"));
				}
			}
			return uuid;
		}
	
	// 将玩家加入权限列表
	public static boolean visitorPlayer(String xuid) {
		if (!isNullOrEmptyString(xuid)) {
			String sops = readToString("permissions.json");
			JSONArray opl = new JSONArray();
			boolean finded = false;
			if (sops != null && sops != "") {
				opl = JSON.parseArray(sops);
				opl = opl == null ? new JSONArray() : opl;
				for (int i = 0, l = opl.size(); i < l; i++) {
					JSONObject d = (JSONObject) opl.get(i);
					String dxuid = d.getString("xuid");
					if (dxuid.equals(xuid)) { // 找到
						d.put("permission", "visitor");
						finded = true;
						break;
					}
				}
			}
			if (!finded) { // 装入新权限
				JSONObject nd = new JSONObject();
				nd.put("permission", "visitor");
				nd.put("xuid", xuid);
				opl.add(nd);
			}
			saveStringTOFile("permissions.json", opl.toString());
			mapi.runcmd("permission reload"); // 重载权限配置
			return true;
		}
		return false;
	}
	// 从白名单中获取xuid
	public static String getLeftXUID(String name) {
		String wl = "";
		wl = readToString("whitelist.json");
		if (!isNullOrEmptyString(wl)) {
			JSONArray wlal = JSON.parseArray(wl);
			if (wlal != null && wlal.size() > 0) {
				for (int i = 0, l = wlal.size(); i < l; i++) {
					JSONObject d = (JSONObject) wlal.get(i);
					String dname = d.getString("name");
					if (dname.equals(name)) { // 找到
						return d.getString("xuid");
					}
				}
			}
		}
		return null;
	}
			
	// 程序从此处入口
	public static void init(MCJAVAAPI api) {
		mapi = api;
		// 后台监听
		api.addBeforeActListener("onServerCmd", new MCJAVAAPI.EventCab() {
			
			@Override
			public boolean callback(String data) {
				JSONObject jo = JSON.parseObject(data);
				String cmd = jo.getString("cmd").trim();
				if (cmd.toLowerCase().indexOf("visitor") == 0) {	// 可能找到
					String  [] cmds = cmd.split(" ");
					if (cmds[0].toLowerCase().equals("visitor")) {	// 找到
						if (cmds.length > 1) {
							String pname = cmd.substring(7).trim();
							String xuid = getXUID(pname);
							if (xuid != null && xuid != "") {
								// 在线降权
								if (visitorPlayer(xuid)) {
									mapi.sendText(getUUID(pname), "您已被降级权限为访客。");
									api.logout("Visited : " + pname);
									return false;
								}
							} else if (!isNullOrEmptyString(xuid = getLeftXUID(pname))) {
								// 离线降权
								if (visitorPlayer(xuid)) {
									api.logout("玩家 " + pname + " 已被降级权限为访客。");
									return false;
								}
							} else {
								api.logout("未能找到对应玩家。");
							}
						} else {
							api.logout("[vistor] 参数过少。用法：visitor [playername]");
						}
						return false;
					}
				}
				return true;
			}
		});
		api.log("[Visitor] 访客命令已装载（仅限后台）。用法：visitor [playername]");
	}
}
