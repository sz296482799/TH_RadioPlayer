package com.taihua.th_radioplayer.global;

public class Config {
	public static final String SERVER = "192.168.30.138";
	public static final String MAC = "0006f43517e8";
	public static final String SN = "aabbccdd";

    public static final String CLINE_TYPE = ClientType.STB.getName();

	public static final boolean DEBUG = true;
    //public static final String MQTT_SERVER_URL = "tcp://120.77.220.29:1883";
    public static final String MQTT_SERVER_URL = "tcp://127.0.0.1:61613";

	public enum Option {
		get_base_data(1, "get_base_data", "get base info"), //
		get_music_data(2, "get_music_data", "get music list"), //
		get_set_data(3, "get_set_data", "get order list"), //
		get_cloud_data(4, "get_cloud_data", "get downd list"), //
		get_play_action(5, "get_play_action", "get action list"), //
		set_client_init(6, "set_client_init", "init client"), //
		set_box_log(7, "set_box_log", "stb record"),
        get_time(8, "get_time", "get server time"),

        connected(9, "connected", "connect mqtt"),
        disconnected(10, "disconnected", "disconnect mqtt");


		private int id;
		private String name;
		private String value;

		private Option(int id, String name, String value) {
			this.id = id;
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public int getId() {
			return id;
		}

		public String getValue() {
			return value;
		}
	}

	// �豸����
	public enum ClientType {
		STB(1, "stb", "box client"), //
		PC(2, "pc", "pc client");//
		private int id;
		private String name;
		private String value;

		private ClientType(int id, String name, String value) {
			this.id = id;
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public int getId() {
			return id;
		}

		public String getValue() {
			return value;
		}
	}
}
