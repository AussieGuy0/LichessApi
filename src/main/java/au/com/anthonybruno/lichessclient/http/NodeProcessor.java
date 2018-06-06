package au.com.anthonybruno.lichessclient.http;


import com.fasterxml.jackson.databind.node.ObjectNode;

public interface NodeProcessor {

    void processNode(ObjectNode node);
}
