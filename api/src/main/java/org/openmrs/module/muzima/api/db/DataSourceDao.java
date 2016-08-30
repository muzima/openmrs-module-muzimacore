package org.openmrs.module.muzima.api.db;

import org.openmrs.module.muzima.model.DataSource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 */
public interface DataSourceDao extends SingleClassDao<DataSource> {

    /**
     * Return the data source with the given uuid.
     *
     * @param uuid the data source uuid.
     * @return the data source with the matching uuid.
     * @should return data with matching uuid.
     * @should return null when no data with matching uuid.
     */
    DataSource getDataSourceByUuid(final String uuid);

    /**
     * Get data source with matching search term for particular page.
     *
     * @param search     the search term.
     * @param pageNumber the page number.
     * @param pageSize   the size of the page.
     * @return list of data source for the page.
     */
    List<DataSource> getPagedDataSources(final String search, final Integer pageNumber, final Integer pageSize);

    /**
     * Get the total number of data source with matching search term.
     *
     *
     * @param search the search term.
     * @return total number of data source in the database.
     */
    Number countDataSource(final String search);
}
