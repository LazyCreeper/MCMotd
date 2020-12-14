package shrbox.github.mcmotd;

import com.google.gson.Gson;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageUtils;

public class Thread extends java.lang.Thread {
    GroupMessageEvent e;
    public void boot(GroupMessageEvent event) {
        e = event;
        start();
    }
    @Override
    public void run() {
        String msg = e.getMessage().contentToString();
        String domain = msg.replace("#motd", "").toLowerCase().trim();
        if (e.getMessage().contentToString().equals("")) {
            e.getGroup().sendMessage(MessageUtils.newChain(new At(e.getSender()))
                    .plus("请输入服务器IP地址"));
            return;
        }
        String port = "25565";
        if (domain.contains(":")) {
            port = msg.split(":")[1];
        }
        String Json = Connection.getURL(domain, port);
        if (Json.equals("")) {
            e.getGroup().sendMessage(("[MCAPI] 接口无响应．．．"));
            return;
        }
        Gson gson = new Gson();
        Serverinfo serverinfo = gson.fromJson(Json, Serverinfo.class);
        if (serverinfo.status.equals("offline")) {
            e.getGroup().sendMessage("[MCAPI] 服务器不在线");
            return;
        }
        e.getGroup().sendMessage("[MCAPI]\nMotd: " + serverinfo.motd + "\n协议版本: " + serverinfo.platform + "\n游戏版本: " + serverinfo.version + "\n在线: " + serverinfo.players_online + "/" + serverinfo.players_max + "\n游戏模式: " + serverinfo.gamemode);
    }
}
