package port_reservation_api;

import java.time.Instant;
import java.util.UUID;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utilities.ExcelReader;

public class Port_Reservation_Api {

    @Test
    public void reserveGetAndReleasePort() {

        RestAssured.useRelaxedHTTPSValidation();

        String filePath = System.getProperty("user.dir")
                + "/src/test/resources/Port_Reservation_Data.xlsx";

        // ==========================================
        // READ DATA FROM EXCEL
        // ==========================================

        String addressIdentifier =
                ExcelReader.getCellDataByColumnName(
                        filePath,
                        "Sheet1",
                        1,
                        "addressIdentifier");

        String siteReference =
                ExcelReader.getCellDataByColumnName(
                        filePath,
                        "Sheet1",
                        1,
                        "siteReference");

        String workOrderNumber =
                ExcelReader.getCellDataByColumnName(
                        filePath,
                        "Sheet1",
                        1,
                        "workOrderNumber");

        String workType =
                ExcelReader.getCellDataByColumnName(
                        filePath,
                        "Sheet1",
                        1,
                        "workType");

        String assignedInstaller =
                ExcelReader.getCellDataByColumnName(
                        filePath,
                        "Sheet1",
                        1,
                        "assignedInstaller");

        // ==========================================
        // STEP 1 - RESERVE PORT POST API
        // ==========================================

        String reserveRequestId =
                UUID.randomUUID().toString();

        String reserveTimestamp =
                Instant.now().toString();

        JSONObject reserveRequestBody =
                new JSONObject();

        reserveRequestBody.put(
                "addressIdentifier",
                Long.parseLong(addressIdentifier));

        reserveRequestBody.put(
                "siteReference",
                Integer.parseInt(siteReference));

        reserveRequestBody.put(
                "Lifecyclestate",
                "Reserved");

        reserveRequestBody.put(
                "workOrderNumber",
                Long.parseLong(workOrderNumber));

        reserveRequestBody.put(
                "workType",
                workType);

        reserveRequestBody.put(
                "assignedInstaller",
                Integer.parseInt(assignedInstaller));

        System.out.println("========== RESERVE REQUEST ==========");
        System.out.println(reserveRequestBody.toString(4));

        Response reserveResponse = RestAssured
                .given()
                .header("MWH-requestID",
                        reserveRequestId)
                .header("MWH-requestTimestamp",
                        reserveTimestamp)
                .header("MWH-originatorID",
                        "Middleware")
                .header("MWH-AppID",
                        "IIPUSER")
                .header("Content-Type",
                        "application/json")
                .body(reserveRequestBody.toString())

                .when()
                .post("https://nginx-waf-test.msa-n.systems.private/itp2g/msa/test2/uk/msa/biz/v1/cabinetport/manage");

        System.out.println("========== RESERVE RESPONSE ==========");
        System.out.println(reserveResponse.asPrettyString());

        Assert.assertEquals(
                reserveResponse.getStatusCode(),
                200,
                "Reserve API Failed");

        // ==========================================
        // STEP 2 - GET PORT DETAILS API
        // ==========================================

        String getRequestId =
                UUID.randomUUID().toString();

        String getTimestamp =
                Instant.now().toString();

        Response getResponse = RestAssured
                .given()
                .header("MWH-requestID",
                        getRequestId)
                .header("MWH-requestTimestamp",
                        getTimestamp)
                .header("MWH-originatorID",
                        "Middleware")
                .header("MWH-AppID",
                        "IIPUSER")
                .header("Content-Type",
                        "application/json")
                .queryParam(
                        "addressIdentifier",
                        addressIdentifier)
                .queryParam(
                        "siteReference",
                        siteReference)

                .when()
                .get("https://nginx-waf-test.msa-n.systems.private/itp2g/msa/test2/uk/msa/biz/v1/cabinetport/get");

        System.out.println("========== GET RESPONSE ==========");
        System.out.println(getResponse.asPrettyString());

        Assert.assertEquals(
                getResponse.getStatusCode(),
                200,
                "GET API Failed");

        // ==========================================
        // EXTRACT VALUES FROM GET RESPONSE
        // ==========================================

        String l4Cabinet =
                getResponse.jsonPath()
                        .getString("l4Cabinet");

        String l4SplitterId =
                getResponse.jsonPath()
                        .getString("l4SplitterId");

        String l4SplitterPortId =
                getResponse.jsonPath()
                        .getString("l4SplitterPortId");

        String l4SplitterPortStatus =
                getResponse.jsonPath()
                        .getString("l4SplitterPortStatus");

        System.out.println("l4Cabinet : "
                + l4Cabinet);

        System.out.println("l4SplitterId : "
                + l4SplitterId);

        System.out.println("l4SplitterPortId : "
                + l4SplitterPortId);

        System.out.println("l4SplitterPortStatus : "
                + l4SplitterPortStatus);

        // ==========================================
        // STEP 3 - RELEASE PORT POST API
        // ==========================================

        String releaseRequestId =
                UUID.randomUUID().toString();

        String releaseTimestamp =
                Instant.now().toString();

        JSONObject releaseRequestBody =
                new JSONObject();

        releaseRequestBody.put(
                "addressIdentifier",
                Long.parseLong(addressIdentifier));

        releaseRequestBody.put(
                "siteReference",
                Integer.parseInt(siteReference));

        releaseRequestBody.put(
                "unusedL4cabinet",
                l4Cabinet);

        releaseRequestBody.put(
                "unusedL4SplitterId",
                l4SplitterId);

        releaseRequestBody.put(
                "unusedL4SplitterPortId",
                l4SplitterPortId);

        releaseRequestBody.put(
                "unusedL4SplitterPortStatus",
                "Reserved");

        releaseRequestBody.put(
                "workOrderNumber",
                Long.parseLong(workOrderNumber));

        releaseRequestBody.put(
                "workType",
                workType);

        releaseRequestBody.put(
                "assignedInstaller",
                Integer.parseInt(assignedInstaller));

        System.out.println("========== RELEASE REQUEST ==========");
        System.out.println(releaseRequestBody.toString(4));

        Response releaseResponse = RestAssured
                .given()
                .header("MWH-requestID",
                        releaseRequestId)
                .header("MWH-requestTimestamp",
                        releaseTimestamp)
                .header("MWH-originatorID",
                        "Middleware")
                .header("MWH-AppID",
                        "VMOOTOA")
                .header("Content-Type",
                        "application/json")
                .body(releaseRequestBody.toString())

                .when()
                .post("https://nginx-waf-test.msa-n.systems.private/itp2g/msa/test2/uk/msa/biz/v1/cabinetport/set");

        String code = releaseResponse.jsonPath().getString("code");

        System.out.println("========== RELEASE RESPONSE ==========");
        System.out.println(releaseResponse.asPrettyString());

        if ("10-1-200000".equals(code)) {
            System.out.println("The Port has been Reserved Successfuly !");
        } else {
            System.out.println("Unexpected response received");
        }
}}