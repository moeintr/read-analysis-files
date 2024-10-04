package org.example;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    private static Logger log = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        List<OrganizationType> organizationTypes = getOrganizationTypes();
        List<Organization> organizations = getOrganizations(organizationTypes);
        List<Bill> billList = readBill(organizationTypes, organizations);
        Map<Organization, OrganizationData> organizationData = countOrganizationsAndSumAmounts(billList);
        reportCountOrganizationsAndSumAmounts(organizationData);
    }
    public static String reportCountOrganizationsAndSumAmounts(Map<Organization, OrganizationData> organizationData) {
        StringBuilder dataReport = new StringBuilder("");
        for (Map.Entry<Organization, OrganizationData> entry : organizationData.entrySet()) {
            String result = entry.getKey().getOrganizationType().getName()+entry.getKey().getCompanyCode() + ","+entry.getKey().getAccount()+"," + entry.getValue().getCount()+","+entry.getValue().getTotalAmount();
            dataReport.append(result+"\n");
        }
        writeFile(dataReport.toString(),"out.report1.txt");
        return dataReport.toString();
    }
    public static Map<Organization, OrganizationData> countOrganizationsAndSumAmounts(List<Bill> billList) {
        Map<Organization, OrganizationData> organizationData = new HashMap<>();

        billList.stream().forEach(bill -> organizationData.computeIfAbsent(bill.getOrganization(), k -> new OrganizationData())
                                                                                                                .incrementCount()
                                                                                                                .addAmount(bill.getAmount()));
        /*for (Bill bill : billList) {
            organizationData.computeIfAbsent(bill.getOrganization(), k -> new OrganizationData())
                    .incrementCount()
                    .addAmount(bill.getAmount());
        }*/

        return organizationData;
    }
    private static List<OrganizationType> getOrganizationTypes() throws IOException {
        String json = "";

        try (InputStream inputStreamOrgEnum = Main.class.getResourceAsStream("/05_organization_enum");
             InputStreamReader streamReaderOrgEnum = new InputStreamReader(inputStreamOrgEnum, StandardCharsets.UTF_8);
             BufferedReader readerOrgEnum = new BufferedReader(streamReaderOrgEnum))
        {
            for (String line; (line = readerOrgEnum.readLine()) != null;) {
                json = json+line;
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<OrganizationType> organizationTypes = objectMapper.readValue(json, new TypeReference<List<OrganizationType>>() {});

        return organizationTypes;
    }

    private static List<Bill> readBill(List<OrganizationType> organizationTypes, List<Organization> organizations) throws IOException {
        StringBuilder errorsString = new StringBuilder("");
        List<Bill> bills = new ArrayList<>();
        int i = 1;
        int j = 1;
        try (InputStream inputStreamBill = Main.class.getResourceAsStream("/01_bill");
             InputStreamReader streamReaderBill = new InputStreamReader(inputStreamBill, StandardCharsets.UTF_8);
             BufferedReader readerBill = new BufferedReader(streamReaderBill))
        {
            for (String line; (line = readerBill.readLine()) != null;) {
                if (i>2) {
                    String lineNum = String.format("%03d", j);
                    try
                    {
                        JSONObject jsonObject = new JSONObject(line);
                        String billId = jsonObject.getString("billId");
                        String amount = jsonObject.getString("amount");
                        String paymentId = null;
                        if (jsonObject.has("paymentId"))
                        {
                            paymentId = jsonObject.getString("paymentId");
                        }
                        String lastFiveDigits = billId.substring(billId.length() - 5);
                        String companyCodeOfBill = lastFiveDigits.substring(0, 3);
                        Integer organizationType = Integer.valueOf(lastFiveDigits.substring(3,4));

                        Organization organization = findOrgByCompanyCodeAndOrgType(companyCodeOfBill, organizationType, organizations);

                        if (Objects.isNull(organization)) {
                            String orgType = organizationTypes.stream()
                                    .filter(type -> type.getOrganizationType().equals(organizationType))
                                    .findFirst().get().getName();
                            String invalidError = "line " +lineNum+ ": invalid organization "+orgType+" "+companyCodeOfBill+"";
                            log.error(invalidError);
                            errorsString.append(invalidError+"\n");
                        } else if (organization.isEnable() == false) {
                            String notEnabledError = "line "+lineNum+": organization "+organization.getOrganizationType().getName()+" "+companyCodeOfBill+" not enabled";
                            log.error(notEnabledError);
                            errorsString.append(notEnabledError+"\n");
                        } else if (!Objects.isNull(organization)) {
                            bills.add(new Bill().setBillId(billId)
                                    .setAmount(Long.parseLong(amount))
                                    .setOrganization(organization));
                        }
                    } catch (JSONException e) {
                        String notParsedError = "line "+lineNum+" not parsed";
                        log.error(notParsedError);
                        errorsString.append(notParsedError+"\n");
                    }
                    j++;
                }
                i++;
            }
        }
        writeFile(errorsString.toString(),"out.validate.txt");
        return bills;
    }
    private static void writeFile(String content, String fileName) {
        File file1 = new File(fileName);
        try (FileWriter fileWriter = new FileWriter(file1)){
            fileWriter.flush();
            fileWriter.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static Organization findOrgByCompanyCodeAndOrgType(String companyCode, Integer orgType, List<Organization> organizations) {
        Organization organization = organizations.stream().filter(organ -> organ.getCompanyCode().equals(companyCode)
                                                                        && organ.getOrganizationType().getOrganizationType().equals(orgType))
                                                          .findFirst()
                                                          .get();
        /*Organization org = null;
        for (Organization organization : organizations)
        {
            if (organization.getCompanyCode().equals(companyCode) && organization.getOrganizationType().getOrganizationType().equals(orgType))
            {
                org = organization;
            }
        }*/
        return organization;
    }
    private static List<Organization> getOrganizations(List<OrganizationType> organizationTypes) throws IOException {
        List<Organization> organizationList = new ArrayList<>();

        try (InputStream inputStreamOrg = Main.class.getResourceAsStream("/02_organization");
             InputStreamReader streamReaderOrg = new InputStreamReader(inputStreamOrg, StandardCharsets.UTF_8);
             BufferedReader readerOrg = new BufferedReader(streamReaderOrg))
        {

            int i1 = 1;
            for (String line; (line = readerOrg.readLine()) != null;) {
                if (i1>2 && !line.isEmpty()){
                    String[] strings = line.split(",");
                    organizationList.add(new Organization()
                            .setOrganizationType(organizationTypes.stream().filter(type -> type.getName().equals(strings[0])).findFirst().get())
                            .setEnable(strings[1].equals("1") ? true : false)
                            .setCompanyCode(strings[2])
                            .setAccount(Long.parseLong(strings[3]))
                            .setName(strings[4]));
                }
                i1++;
            }
        }
        return organizationList;
    }
}