package team.mzgl.mods.testcase;

import java.util.ArrayList;

import com.alibaba.fastjson.*;

import BDS.MCJAVAAPI;

public class SimpleGUI {
	private MCJAVAAPI mapi;
	/**
	 * 表单处理选择程序接口
	 */
	public interface ONSELECT {
		/**
		 * 表单结果回调
		 * @param selected - 表单选择内容
		 */
		void onSelected(String selected);
	}
	/**
	 * 表单超时程序接口
	 */
	public interface ONTIMEOUT {
		/**
		 * 表单超时回调
		 */
		void onTimeout();
	}
	/**
	 * 表单id
	 */
	public int id;
	/**
	 * 玩家uuid
	 */
	public String uuid;
	/**
	 * 返回的选项
	 */
	public String selected;
	/**
	 * 表单标题
	 */
	public String title;
	/**
	 * 内容
	 */
	public String content;
	/**
	 * 按钮内容
	 */
	public ArrayList<String> buttons;
	/**
	 * 选择处理
	 */
	public ONSELECT onselected;
	/**
	 * 超时处理
	 */
	public ONTIMEOUT ontimeout;
	/**
	 * 超时毫秒数
	 */
	public int timeout;
	/**
	 * 注册回调
	 */
	public MCJAVAAPI.EventCab fmcb;
	/**
	 * 是否取消
	 */
	public boolean canceled;
	/**
	 * 创建一个简单表单
	 * @param api
	 * @param u - 玩家uuid
	 * @param t - 标题
	 * @param c - 内容
	 * @param b - 按钮列表
	 */
	public SimpleGUI(MCJAVAAPI api, String u, String t, String c, ArrayList<String> b) {
		mapi = api;
		uuid = u;
		title = t;
		content = c;
		buttons = (b == null ? new ArrayList<String>() : b);
	}
	/**
	 * 开始超时监听
	 */
	public void startTimeout() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(timeout);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!canceled) {
              		mapi.releaseForm(id);
              		mapi.removeBeforeActListener("onFormSelect", fmcb);
              		if (ontimeout != null)
              			ontimeout.onTimeout();
              	}
			}
		});
		t.start();
	}
	/**
	 * 取消一个超时监听
	 */
	public void cancelTimeout() {
		canceled = true;
	}
	/**
	 * 发送一个简易表单
	 * @param tout - 超时时间
	 * @param func - 主选择处理函数
	 * @param tofunc - 超时处理函数
	 * @return 是否发送成功
	 */
	public boolean send(int tout, ONSELECT func, ONTIMEOUT tofunc) {
		timeout = tout;
		onselected = func;
		ontimeout = tofunc;
		fmcb = new MCJAVAAPI.EventCab() {
			@Override
			public boolean callback(String data) {
				JSONObject jo = JSON.parseObject(data);
				if (jo.getIntValue("formid") == id) {	// 确定是当前表单
					mapi.removeBeforeActListener("onFormSelect", fmcb);
					cancelTimeout();
					onselected.onSelected(jo.getString("selected"));
				}
				return true;
			}
		};
		mapi.addBeforeActListener("onFormSelect", fmcb);
		String bts = "[]";
		if (buttons != null && buttons.size() > 0) {
			bts = JSON.toJSONString(buttons);
		}
		id = mapi.sendSimpleForm(uuid, title, content, bts);
		boolean ret = (id != 0);
		if (timeout > 0) {
			startTimeout();
		}
		return ret;
	}
}
