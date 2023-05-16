package io.mosip.imagedecoder.model.wsq;

import io.mosip.imagedecoder.constant.wsq.WsqConstant;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
/*
 * A distributed hash table (DHT) is a distributed system that provides a lookup
 * service similar to a hash table: key–value pairs are stored in a DHT, and any
 * participating node can efficiently retrieve the value associated with a given
 * key. The main advantage of a DHT is that nodes can be added or removed with
 * minimum work around re-distributing keys. Keys are unique identifiers which
 * map to particular values, which in turn can be anything from addresses, to
 * documents, to arbitrary data.[1] Responsibility for maintaining the mapping
 * from keys to values is distributed among the nodes, in such a way that a
 * change in the set of participants causes a minimal amount of disruption. This
 * allows a DHT to scale to extremely large numbers of nodes and to handle
 * continual node arrivals, departures, and failures.
 * 
 */
public class WsqTableDht {
	private int tableDef;
	private int[] huffBits = new int[WsqConstant.MAX_HUFFBITS];
	private int[] huffValues = new int[WsqConstant.MAX_HUFFCOUNTS_WSQ + 1];
}
