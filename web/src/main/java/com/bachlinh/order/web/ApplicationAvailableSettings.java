package com.bachlinh.order.web;

public interface ApplicationAvailableSettings extends org.hibernate.cfg.AvailableSettings {
    String HIBERNATE_SEARCH_ENABLE = "hibernate.search.enabled";
    String HIBERNATE_SEARCH_MAPPING_ENABLE = "hibernate.search.mapping.process_annotations";
    String HIBERNATE_SEARCH_DIRECTORY_ROOT = "hibernate.search.backend.directory.root";
    String HIBERNATE_SEARCH_DIRECTORY_TYPE = "hibernate.search.backend.directory.type";
    String HIBERNATE_SEARCH_DIRECTORY_ACCESS_STRATEGY = "hibernate.search.backend.directory.filesystem_access.strategy";
    String HIBERNATE_SEARCH_DIRECTORY_LOCKING_STRATEGY = "hibernate.search.backend.directory.locking.strategy";
    String HIBERNATE_SEARCH_DIRECTORY_SHARDING_STRATEGY = "hibernate.search.backend.sharding.strategy";
    String HIBERNATE_SEARCH_DIRECTORY_SHARDING_OF_SHARDS = "hibernate.search.backend.sharding.number_of_shards";
    String HIBERNATE_SEARCH_DIRECTORY_SHARDING_STRATEGY_IDENTIFIERS = "hibernate.search.backend.sharding.shard_identifiers";
    String HIBERNATE_ALLOW_UPDATE_OUT_SIDE_TRANSACTION = "hibernate.allow_update_outside_transaction";
}
