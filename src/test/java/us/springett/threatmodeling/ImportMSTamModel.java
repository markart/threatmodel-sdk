package us.springett.threatmodeling;

import org.junit.Before;
import org.junit.Test;
import us.springett.threatmodeling.exception.ParseException;
import us.springett.threatmodeling.model.Asset;
import us.springett.threatmodeling.model.Threat;
import us.springett.threatmodeling.model.ThreatModel;
import us.springett.threatmodeling.tools.mstmt2016.util.ParseUtil;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ImportMSTamModel {

    ThreatModel threatModel;

    @Before
    public void setup() throws ParseException {
        File file = new File("src/test/resources/mstam2016.tm7");
        threatModel = new ThreatModelFactory().parse(file, ThreatModelingTool.MICROSOFT_THREAT_MODELING_TOOL_2016);
    }

    @Test
    public void testAssignThreatsToAssets() {
        Map<String,Asset> assetIdMap = ParseUtil.mapAssetsByIds(threatModel.getAssets());
        Asset webAppAsset = assetIdMap.get("5a9a6139-22c2-4933-aecd-5f9ab30af5d5");
        Asset databaseAsset = assetIdMap.get("b10596d0-ef74-438a-8ec5-7a0ecabf3414");

        assertThat(threatModel.getAssets().size(), equalTo(3));

        //The first 10 threats belong to the web app
        List<Threat> firstTenThreats = threatModel.getThreats().subList(0,9);
        for (Threat threat : firstTenThreats) {
            assertThat(threat.getAssets().size(), equalTo(1));
            assertThat(threat.getAssets().get(0).getId(), equalTo(webAppAsset.getId()));
        }

        //Last 2 threats belong to the database
        List<Threat> lastTwoThreats = threatModel.getThreats().subList(10,11);
        for (Threat threat : lastTwoThreats) {
            assertThat(threat.getAssets().size(), equalTo(1));
            assertThat(threat.getAssets().get(0).getId(), equalTo(databaseAsset.getId()));
        }

    }
}
