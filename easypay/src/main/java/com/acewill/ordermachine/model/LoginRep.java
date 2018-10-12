package com.acewill.ordermachine.model;

public class LoginRep extends BaseModelSz{
	public String terminalid;// ：终端的ＩＤ
	public int version;// ： 最新的版本号
	public String updatetime;// ： 最新的版本更新时间
	public String downloadpath;// ：安装（升级）文件的下载地址
	public String description;// ：描述
	public String token;// ： 令牌

	public String getTerminalid() {
		return terminalid;
	}

	public void setTerminalid(String terminalid) {
		this.terminalid = terminalid;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public String getDownloadpath() {
		return downloadpath;
	}

	public void setDownloadpath(String downloadpath) {
		this.downloadpath = downloadpath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "LoginRep{" +
				"terminalid='" + terminalid + '\'' +
				", version=" + version +
				", updatetime='" + updatetime + '\'' +
				", downloadpath='" + downloadpath + '\'' +
				", description='" + description + '\'' +
				", token='" + token + '\'' +
				'}';
	}
}
