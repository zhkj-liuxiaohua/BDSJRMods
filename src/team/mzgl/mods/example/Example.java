package team.mzgl.mods.example;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.*;

import BDS.MCJAVAAPI;

public class Example {
	
	public static String TimeNow() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}
	
	public static class XYZ {
		public float x;
		public float y;
		public float z;
	}
	public static class Pos3 {
		public int x;
		public int y;
		public int z;
	}
	
	public static void init(MCJAVAAPI api) {
		// 放置方块监听
		api.addAfterActListener("onPlacedBlock", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				if (je.getBooleanValue("RESULT")) {
					String tnow = TimeNow();
					XYZ xyz = je.getObject("XYZ", XYZ.class);
					Pos3 pos = je.getObject("position", Pos3.class);
					String str = '[' + tnow + " AfterPlaceBlock] " + "玩家 " + je.getString("playername") +
							" 于(" + xyz.x + ',' + xyz.y + ',' + xyz.z + ")位置" +
							(!je.getBooleanValue("isstand") ? " 悬空地" : "") + " 在 " + je.getString("dimension") +
							" (" + pos.x + ',' + pos.y + ',' + pos.z + ") 处放置了"
							+ je.getString("blockname") + " 方块。";
					api.log(str);
				}
				return true;
			}
		});
		// 使用物品监听
		api.addAfterActListener("onUseItem", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				if (je.getBooleanValue("RESULT")) {
					String tnow = TimeNow();
					XYZ xyz = je.getObject("XYZ", XYZ.class);
					Pos3 pos = je.getObject("position", Pos3.class);
					String str = '[' + tnow + " AfterUseItem] " + "玩家 " + je.getString("playername") +
							" 于(" + xyz.x + ',' + xyz.y + ',' + xyz.z + ")位置" +
							(!je.getBooleanValue("isstand") ? " 悬空地" : "") + " 在 " + je.getString("dimension") +
							" (" + pos.x + ',' + pos.y + ',' + pos.z + ") 处使用了"
							+ je.getString("itemname") + " 物品。";
					api.log(str);
				}
				return false;
			}
		});
		// 破坏方块后触发回调
		api.addAfterActListener("onDestroyBlock", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				if (je.getBooleanValue("RESULT")) {
					String tnow = TimeNow();
					XYZ xyz = je.getObject("XYZ", XYZ.class);
					Pos3 pos = je.getObject("position", Pos3.class);
					String str = '[' + tnow + " AfterDestroyBlock] " + "玩家 " + je.getString("playername") +
							" 于(" + xyz.x + ',' + xyz.y + ',' + xyz.z + ")位置" +
							(!je.getBooleanValue("isstand") ? " 悬空地" : "") + " 在 " + je.getString("dimension") +
							" (" + pos.x + ',' + pos.y + ',' + pos.z + ") 处破坏了"
							+ je.getString("blockname") + " 方块。";
					api.log(str);
				}
				return true;
			}
		});
		// 开箱监听
		api.addBeforeActListener("onStartOpenChest", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				String tnow = TimeNow();
				XYZ xyz = je.getObject("XYZ", XYZ.class);
				Pos3 pos = je.getObject("position", Pos3.class);
				String str = '[' + tnow + " BeforeOpenChest] " + "玩家 " + je.getString("playername") +
							" 于(" + xyz.x + ',' + xyz.y + ',' + xyz.z + ")位置" +
							(!je.getBooleanValue("isstand") ? " 悬空地" : "") + " 试图在 " + je.getString("dimension") +
							" (" + pos.x + ',' + pos.y + ',' + pos.z + ") 处打开箱子。";
				api.log(str);
				return true;	// 此处或可添加坐标判断实现锁箱操作
			}
		});
		// 开桶监听
		api.addBeforeActListener("onStartOpenBarrel", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				String tnow = TimeNow();
				XYZ xyz = je.getObject("XYZ", XYZ.class);
				Pos3 pos = je.getObject("position", Pos3.class);
				String str = '[' + tnow + " BeforeOpenBarrel] " + "玩家 " + je.getString("playername") +
							" 于(" + xyz.x + ',' + xyz.y + ',' + xyz.z + ")位置" +
							(!je.getBooleanValue("isstand") ? " 悬空地" : "") + " 试图在 " + je.getString("dimension") +
							" (" + pos.x + ',' + pos.y + ',' + pos.z + ") 处打开木桶。";
				api.log(str);
				return true;
			}
		});
		// 关箱监听
		api.addAfterActListener("onStopOpenChest", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				String tnow = TimeNow();
				XYZ xyz = je.getObject("XYZ", XYZ.class);
				Pos3 pos = je.getObject("position", Pos3.class);
				String str = '[' + tnow + " AfterStopOpenChest] " + "玩家 " + je.getString("playername") +
							" 于(" + xyz.x + ',' + xyz.y + ',' + xyz.z + ")位置" +
							(!je.getBooleanValue("isstand") ? " 悬空地" : "") + " 在 " + je.getString("dimension") +
							" (" + pos.x + ',' + pos.y + ',' + pos.z + ") 处关闭了箱子。";
				api.log(str);
				return true;
			}
		});
		// 关桶监听
		api.addAfterActListener("onStopOpenBarrel", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				String tnow = TimeNow();
				XYZ xyz = je.getObject("XYZ", XYZ.class);
				Pos3 pos = je.getObject("position", Pos3.class);
				String str = '[' + tnow + " AfterStopOpenBarrel] " + "玩家 " + je.getString("playername") +
							" 于(" + xyz.x + ',' + xyz.y + ',' + xyz.z + ")位置" +
							(!je.getBooleanValue("isstand") ? " 悬空地" : "") + " 在 " + je.getString("dimension") +
							" (" + pos.x + ',' + pos.y + ',' + pos.z + ") 处关闭了木桶。";
				api.log(str);
				return true;
			}
		});
		// 放入取出物品监听
		api.addAfterActListener("onSetSlot", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				String tnow = TimeNow();
				XYZ xyz = je.getObject("XYZ", XYZ.class);
				Pos3 pos = je.getObject("position", Pos3.class);
				int itemcount = je.getIntValue("itemcount");
				String str = '[' + tnow + " AfterSetSlot] " + "玩家 " + je.getString("playername") +
							" 于(" + xyz.x + ',' + xyz.y + ',' + xyz.z + ")位置" +
							(!je.getBooleanValue("isstand") ? " 悬空地" : "") + " 在 " + je.getString("dimension") +
							" (" + pos.x + ',' + pos.y + ',' + pos.z + ") 处的" +
							je.getString("blockname") + "内的第 " + je.getIntValue("slot") + " 槽内 "+
							(itemcount > 0 ? "放入了 " + itemcount + " 个 " + je.getString("itemname") + " 物品。" :
									"取出了物品。");
				api.log(str);
				return true;
			}
		});
		// 切换维度监听
		api.addAfterActListener("onChangeDimension", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				String tnow = TimeNow();
				XYZ xyz = je.getObject("XYZ", XYZ.class);
				String str = '[' + tnow + " AfterChangeDimension] " + "玩家 " + je.getString("playername") +
						(!je.getBooleanValue("isstand") ? " 悬空地" : "") + " 切换维度至 " + je.getString("dimension") +
						" (" + xyz.x + ',' + xyz.y + ',' + xyz.z + ") 处。";
				api.log(str);
				return true;
			}
		});
		// 命名生物死亡监听
		api.addAfterActListener("onMobDie", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				String mobname = je.getString("mobname");
				if (mobname != null && !"".equals(mobname)) {
					String tnow = TimeNow();
					String str = "[" + tnow + " DeathInfo] " + mobname + " 被 " + je.getString("srcname") + " 杀死了";
					api.log(str);
				}
				return false;
			}
		});
		// 聊天消息
		api.addAfterActListener("onChat", new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject je = JSON.parseObject(data);
				String chatstyle = je.getString("chatstyle");
				if (!"title".equals(chatstyle)) {
					String tnow = TimeNow();
					String target = je.getString("target");
					String str = "[" + tnow + " Chat] " + je.getString("playername") +
						((target != null && !"".equals(target)) ? " 悄悄地对 " + target : "") +
						" 说: " + je.getString("msg");
					api.log(str);
				}
				return false;
			}
		});
		
		api.log("[Example] 日志测试已加载。");
	}
}
