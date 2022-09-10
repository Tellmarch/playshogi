package com.playshogi.website.javaclient;

import com.gdevelop.gwt.syncrpc.SyncProxy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.playshogi.website.gwt.shared.models.PositionDetails;
import com.playshogi.website.gwt.shared.services.KifuService;
import com.playshogi.website.gwt.shared.services.KifuServiceAsync;

public class Openings {

    private static KifuServiceAsync kifuServiceAsync;

    public static KifuServiceAsync getKifuServiceAsync() {
        if (kifuServiceAsync == null) {
            SyncProxy.setBaseURL("https://playshogi.com/PlayShogiWebsite/");

            kifuServiceAsync = SyncProxy.create(KifuService.class);
        }
        return kifuServiceAsync;
    }

    public static void main(String[] args) {
        String sfen = "lnsgkgsnl/1r5b1/pppppp1pp/6p2/7P1/9/PPPPPPP1P/1B5R1/LNSGKGSNL w -";

        Openings.getKifuServiceAsync().getPositionDetails(sfen, "1", new AsyncCallback<PositionDetails>() {
            @Override
            public void onFailure(final Throwable throwable) {
                System.out.println("error");
            }

            @Override
            public void onSuccess(final PositionDetails positionDetails) {
                System.out.println(positionDetails);
            }
        });
    }
}
