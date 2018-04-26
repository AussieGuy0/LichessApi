package au.com.anthonybruno.lichessclient;

import static au.com.anthonybruno.lichessclient.LichessClient.BASE_URL;

public enum URLS {

    ACCOUNT("account"), BOT("bot"), CHALLENGE("challenge");

    private final String url;

    URLS(String url) {
        this.url = BASE_URL + "/" + url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return url;
    }
}
