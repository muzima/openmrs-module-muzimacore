/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.muzima.api.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.muzima.model.ArchiveData;
import org.openmrs.module.muzima.model.DataSource;
import org.openmrs.module.muzima.model.ErrorData;
import org.openmrs.module.muzima.model.QueueData;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import java.util.Date;
import java.util.List;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

/**
 */
public class DataServiceTest extends BaseModuleContextSensitiveTest {
    // Datasets
    protected static final String QUEUE_DATA_XML = "datasets/DataServiceTest-QueueData.xml";
    protected static final String DATA_SOURCE_XML = "datasets/DataServiceTest-DataSource.xml";
    protected static final String ERROR_DATA_XML = "datasets/DataServiceTest-ErrorData.xml";
    protected static final String ARCHIVE_DATA_XML = "datasets/DataServiceTest-ArchiveData.xml";
    // Services
    protected static DataService dataService = null;
    //data list
    protected static List<QueueData> queueDataList = null;
    protected static List<ErrorData> errorDataList = null;
    protected static List<ArchiveData> archiveDataList =null;
    protected static List<DataSource> dataSourceList =null;

    /**
     * Run this before each unit test in this class.
     *
     * @throws Exception
     *
     */
    @Before
    public void runBeforeAllTests() throws Exception {
        dataService = Context.getService(DataService.class);
        initializeInMemoryDatabase();
        authenticate();
        executeDataSet(DATA_SOURCE_XML);
    }

    /**
     * @verifies return queue data with matching id.
     * @see DataService#getQueueData(Integer)
     */
    @Test
    public void getQueueData_shouldReturnQueueDataWithMatchingId() throws Exception {
        Assert.assertEquals(null, dataService.getQueueData(1));
        executeDataSet(QUEUE_DATA_XML);
        Assert.assertEquals("Id not matching", new Integer(1), dataService.getQueueData(1).getId());
    }

    /**
     * @verifies return queue data with matching uuid.
     * @see DataService#getQueueDataByUuid(String)
     */
    @Test
    public void getQueueDataByUuid_shouldReturnQueueDataWithMatchingUuid() throws Exception {
        Assert.assertEquals(null, dataService.getQueueDataByUuid("48e55acd-b8db-4f0d-862e-c6969250be2b"));
        executeDataSet(QUEUE_DATA_XML);
        Assert.assertEquals("Id not matching", new Integer (1), dataService.getQueueDataByUuid("48e55acd-b8db-4f0d-862e-c6969250be2b").getId());
    }

    /**
     * @verifies return empty list when no queue data are saved in the database.
     * @see DataService#getAllQueueData()
     */
    @Test
    public void getAllQueueData_shouldReturnEmptyListWhenNoQueueDataAreSavedInTheDatabase() throws Exception {
        queueDataList = dataService.getAllQueueData();
        Assert.assertNotNull("Should return an empty list not null", queueDataList);
        Assert.assertEquals(0, queueDataList.size());
        executeDataSet(QUEUE_DATA_XML);
        queueDataList = dataService.getAllQueueData();
        Assert.assertThat(queueDataList, hasSize(greaterThan(0)));
    }
    /**
     * @verifies save queue data into the database.
     * @see DataService#saveQueueData(org.openmrs.module.muzima.model.QueueData)
     */
    @Test
    public void saveQueueData_shouldSaveQueueDataIntoTheDatabase() throws Exception {
        Assert.assertEquals(null, dataService.getQueueData(1));
        QueueData queueData = new QueueData();
        queueData.setId(1);
        queueData.setUuid("48e55acd-b8db-4f0d-862e-c6969250be2b");
        queueData.setDiscriminator("registration");
        queueData.setDataSource(new DataSource());
        queueData.setPayload("Patient1 registration data");
        queueData.setDateCreated(new Date());
        queueData.setCreator(new User(1));
        QueueData result = dataService.saveQueueData(queueData);
        Assert.assertEquals("Payload not saved", new String("Patient1 registration data"), result.getPayload());
    }
    /**
     * @verifies remove queue data from the database
     * @see DataService#purgeQueueData(org.openmrs.module.muzima.model.QueueData)
     */
    @Test
    public void purgeQueueData_shouldRemoveQueueDataFromTheDatabase() throws Exception {
        QueueData queueData = new QueueData();
        executeDataSet(QUEUE_DATA_XML);
        queueDataList = dataService.getAllQueueData();
//        verify that we have queue data
        Assert.assertThat(queueDataList, hasSize(greaterThan(0)));
//        purge queue data
        dataService.purgeQueueData(dataService.getQueueData(1));
//        verify that we have queue is empty
        Assert.assertEquals("queue data not purged", 0, dataService.getAllQueueData().size());
    }
    /**
     * @verifies return error data with matching id.
     * @see DataService#getErrorData(Integer)
     */
    @Test
    public void getErrorData_shouldReturnErrorDataWithMatchingId() throws Exception {
//        return null when no error data with matching id
        Assert.assertEquals(null, dataService.getErrorData(1));
//         create error data
        executeDataSet(ERROR_DATA_XML);
//        verify that we have error data with matching id
        Assert.assertEquals("Id not matching", new Integer(1), dataService.getErrorData(1).getId());
    }

    /**
     * @verifies return error data with matching uuid.
     * @see DataService#getErrorDataByUuid(String)
     */
    @Test
    public void getErrorDataByUuid_shouldReturnErrorDataWithMatchingUuid() throws Exception {
        Assert.assertEquals(null,dataService.getErrorDataByUuid("48e55acd-b8db-4f0d-862e-c6969250be2b"));
        executeDataSet(ERROR_DATA_XML);
        Assert.assertEquals("Id not matching", new Integer(1), dataService.getErrorDataByUuid("48e55acd-b8db-4f0d-862e-c6969250be2b").getId());
    }

    /**
     * @verifies return empty list when no error data are saved in the database.
     * @see DataService#getAllErrorData()
     */
    @Test
    public void getAllErrorData_shouldReturnEmptyListWhenNoErrorDataAreSavedInTheDatabase() throws Exception {
        errorDataList = dataService.getAllErrorData();
        Assert.assertNotNull("Should return an empty list", errorDataList);
        Assert.assertEquals(0, errorDataList.size());
        executeDataSet(ERROR_DATA_XML);
        errorDataList = dataService.getAllErrorData();
        Assert.assertThat(errorDataList, hasSize(greaterThan(0)));
    }

    /**
     * @verifies save error data into the database.
     * @see DataService#saveErrorData(org.openmrs.module.muzima.model.ErrorData)
     */
    @Test
    public void saveErrorData_shouldSaveErrorDataIntoTheDatabase() throws Exception {
        Assert.assertEquals(null, dataService.getErrorData(1));
        ErrorData errorData = new ErrorData();
        errorData.setId(1);
        errorData.setDiscriminator("registration");
        errorData.setDataSource(new DataSource());
        errorData.setPayload("Patient1 registration data");
        errorData.setMessage("Unable to process queue data");
        errorData.setCreator(new User(1));
        errorData.setDateCreated(new Date());
        errorData.setUuid("48e55acd-b8db-4f0d-862e-c6969250be2b");
        ErrorData result = dataService.saveErrorData(errorData);
        Assert.assertEquals("Payload not saved", new String("Patient1 registration data"), result.getPayload());
    }
    /**
     * @verifies remove error data from the database
     * @see DataService#purgeErrorData(org.openmrs.module.muzima.model.ErrorData)
     */
    @Test
    public void purgeErrorData_shouldRemoveErrorDataFromTheDatabase() throws Exception {
        ErrorData errorData = new ErrorData();
        executeDataSet(ERROR_DATA_XML);
        errorDataList = dataService.getAllErrorData();
//        verify that we have error data
        Assert.assertThat(errorDataList, hasSize(greaterThan(0)));
//        purge error data
        dataService.purgeErrorData(dataService.getErrorData(1));
//        verify that error data is empty
        Assert.assertEquals("error data not purged", 0, dataService.getAllErrorData().size());
    }
    /**
     * @verifies return archive data with matching id.
     * @see DataService#getArchiveData(Integer)
     */
    @Test
    public void getArchiveData_shouldReturnArchiveDataWithMatchingId() throws Exception {
        Assert.assertEquals(null, dataService.getArchiveData(1));
        executeDataSet(ARCHIVE_DATA_XML);
        Assert.assertEquals("Id not matching", new Integer(1), dataService.getArchiveData(1).getId());
    }

    /**
     * @verifies return archive data with matching uuid.
     * @see DataService#getArchiveDataByUuid(String)
     */
    @Test
    public void getArchiveDataByUuid_shouldReturnArchiveDataWithMatchingUuid() throws Exception {
        Assert.assertEquals(null,dataService.getArchiveDataByUuid("48e55acd-b8db-4f0d-862e-c6969250be2b"));
        executeDataSet(ARCHIVE_DATA_XML);
        Assert.assertEquals("Id not matching", new Integer(1), dataService.getArchiveDataByUuid("48e55acd-b8db-4f0d-862e-c6969250be2b").getId());
    }

    /**
     * @verifies return empty list when no archive data are saved in the database.
     * @see DataService#getAllArchiveData()
     */
    @Test
    public void getAllArchiveData_shouldReturnEmptyListWhenNoArchiveDataAreSavedInTheDatabase() throws Exception {
        archiveDataList = dataService.getAllArchiveData();
        Assert.assertNotNull("Should return an empty list", archiveDataList);
        Assert.assertEquals(0, archiveDataList.size());
        executeDataSet(ARCHIVE_DATA_XML);
        archiveDataList = dataService.getAllArchiveData();
        Assert.assertThat(archiveDataList, hasSize(greaterThan(0)));
    }

    /**
     * @verifies return all saved archive data.
     * @see DataService#getAllArchiveData()
     */
    @Test
    public void getAllArchiveData_shouldReturnAllSavedArchiveData() throws Exception {
        executeDataSet(ARCHIVE_DATA_XML);
        archiveDataList = dataService.getAllArchiveData();
        Assert.assertEquals(1, archiveDataList.size());
        Assert.assertEquals("Id not matching", new Integer(1), archiveDataList.get(0).getId());
    }

    /**
     * @verifies save archive data into the database.
     * @see DataService#saveArchiveData(org.openmrs.module.muzima.model.ArchiveData)
     */
    @Test
    public void saveArchiveData_shouldSaveArchiveDataIntoTheDatabase() throws Exception {
        Assert.assertEquals(null, dataService.getArchiveData(1));
        ArchiveData archiveData = new ArchiveData();
        archiveData.setId(1);
        archiveData.setDiscriminator("registration");
        archiveData.setDataSource(new DataSource());
        archiveData.setPayload("registration data");
        archiveData.setMessage("Queue data processed successfully!");
        archiveData.setDateArchived(new Date());
        archiveData.setCreator(new User(1));
        archiveData.setDateCreated(new Date());
        archiveData.setChangedBy(new User(1));
        archiveData.setDateChanged(new Date());
        archiveData.setUuid("48e55acd-b8db-4f0d-862e-c6969250be2b");
        ArchiveData result = dataService.saveArchiveData(archiveData);
        Assert.assertEquals("Payload not saved", new String("registration data"), result.getPayload());
    }

    /**
     * @verifies remove archive data from the database
     * @see DataService#purgeArchiveData(org.openmrs.module.muzima.model.ArchiveData)
     */
    @Test
    public void purgeArchiveData_shouldRemoveArchiveDataFromTheDatabase() throws Exception {
        ArchiveData archiveData = new ArchiveData();
        executeDataSet(ARCHIVE_DATA_XML);
        archiveDataList = dataService.getAllArchiveData();
//        verify that we have archived data
        Assert.assertThat(archiveDataList, hasSize(greaterThan(0)));
//        purge archived data
        dataService.purgeArchiveData(dataService.getArchiveData(1));
//        verify that archived data is empty
        Assert.assertEquals("error data not purged", 0, dataService.getAllArchiveData().size());
    }

    /**
     * @verifies return data source with matching id.
     * @see DataService#getDataSource(Integer)
     */
    @Test
    public void getDataSource_shouldReturnDataSourceWithMatchingId() throws Exception {
//        executeDataSet done @before
        Assert.assertEquals("Id not matching", new Integer(1), dataService.getDataSource(1).getId());
//        purge data source then test for null when no data
        dataService.purgeDataSource(dataService.getDataSource(1));
        Assert.assertEquals(null, dataService.getDataSource(1));
    }
    /**
     * @verifies return data source with matching uuid.
     * @see DataService#getDataSourceByUuid(String)
     */
    @Test
    public void getDataSourceByUuid_shouldReturnDataSourceWithMatchingUuid() throws Exception {
//        executeDataSet done @before
        Assert.assertEquals("Id not matching", new Integer (1), dataService.getDataSourceByUuid("fd352cae-65f0-4197-8b82-1ea49e33cb4a").getId());
//        purge data source then test for null when no data
        dataService.purgeDataSource(dataService.getDataSource(1));
        Assert.assertEquals(null, dataService.getDataSourceByUuid("48e55acd-b8db-4f0d-862e-c6969250be2b"));
    }

    /**
     * @verifies return empty list when no data source are saved in the database.
     * @see DataService#getAllDataSource()
     */
    @Test
    public void getAllDataSource_shouldReturnEmptyListWhenNoDataSourceAreSavedInTheDatabase() throws Exception {
//        executeDataSet done @before
        Assert.assertThat(dataService.getAllDataSource(), hasSize(greaterThan(0)));
        Assert.assertEquals(new Integer(1), dataService.getAllDataSource().get(0).getId());
//        purge data source then test for empty list when no data
        dataService.purgeDataSource(dataService.getDataSource(1));
        dataSourceList = dataService.getAllDataSource();
        Assert.assertNotNull("Should return an empty list not null", dataSourceList);
        Assert.assertEquals(0, dataSourceList.size());
    }

    /**
     * @verifies save data source into the database.
     * @see DataService#saveDataSource(org.openmrs.module.muzima.model.DataSource)
     */
    @Test
    public void saveDataSource_shouldSaveDataSourceIntoTheDatabase() throws Exception {
        DataSource dataSource = new DataSource();
        //        purge data source
        dataService.purgeDataSource(dataService.getDataSource(1));
//        verify that data source is empty
        Assert.assertEquals("error data not purged", 0, dataService.getAllDataSource().size());
        dataSource.setId(1);
        dataSource.setName("registration");
        dataSource.setDescription("Data source for registration");
        dataSource.setCreator(new User(1));
        dataSource.setDateCreated(new Date());
        dataSource.setChangedBy(new User(1));
        dataSource.setDateChanged(new Date());
        dataSource.setRetired(false);
        dataSource.setRetiredBy(new User(1));
        dataSource.setDateRetired(new Date());
        dataSource.setRetireReason("No longer in use");
        dataSource.setUuid("fd352cae-65f0-4197-8b82-1ea49e33cb4a");
        DataSource result = dataService.saveDataSource(dataSource);
        Assert.assertEquals("Data source not saved", new String("Data source for registration"), result.getDescription());
    }
    /**
     * @verifies remove data source from the database
     * @see DataService#purgeDataSource(org.openmrs.module.muzima.model.DataSource)
     */
    @Test
    public void purgeDataSource_shouldRemoveDataSourceFromTheDatabase() throws Exception {
        dataSourceList = dataService.getAllDataSource();
//        verify that we have data source
        Assert.assertThat(dataSourceList, hasSize(greaterThan(0)));
//        purge data source
        dataService.purgeDataSource(dataService.getDataSource(1));
//        verify that data source is empty
        Assert.assertEquals("data source not purged", 0, dataService.getAllDataSource().size());
    }
}