package network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {

	public static final int PORT = 54555;
	
	public static void register (EndPoint endPoint) {
		
		Kryo kryo = endPoint.getKryo();
		
		kryo.register(Packet.class);
		kryo.register(LoginPacket.class);
		kryo.register(StartPacket.class);
		kryo.register(DirectionPacket.class);
		kryo.register(CreatePlayerPacket.class);
		kryo.register(PlayerPacket.class);
		kryo.register(PelletPacket.class);
		kryo.register(DisconnectPacket.class);
	}
}
