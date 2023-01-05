package com.playshogi.website.javaclient;

import com.gdevelop.gwt.syncrpc.SyncProxy;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.playshogi.website.gwt.shared.models.ProblemDetails;
import com.playshogi.website.gwt.shared.services.ProblemsService;
import com.playshogi.website.gwt.shared.services.ProblemsServiceAsync;

public class DailyTsume {

    private static ProblemsServiceAsync problemsServiceAsync;

    public static ProblemsServiceAsync getProblemsServiceAsync() {
        if (problemsServiceAsync == null) {
            SyncProxy.setBaseURL("https://playshogi.com/PlayShogiWebsite/");

            problemsServiceAsync = SyncProxy.create(ProblemsService.class);
        }
        return problemsServiceAsync;
    }

    public static void main(String[] args) {
        String sfen = "lnsgkgsnl/1r5b1/pppppp1pp/6p2/7P1/9/PPPPPPP1P/1B5R1/LNSGKGSNL w -";

        DailyTsume.getProblemsServiceAsync().getRandomProblem(7, new AsyncCallback<ProblemDetails>() {
            @Override
            public void onFailure(final Throwable throwable) {
                System.out.println("error");
            }

            @Override
            public void onSuccess(final ProblemDetails problemDetails) {
                System.out.println(problemDetails);
            }
        });
    }
}
