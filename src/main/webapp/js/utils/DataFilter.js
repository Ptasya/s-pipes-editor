'use strict';

/**
 * Filters data according to specified filter(s).
 *
 * The filtering supports property paths - separated by dots, i.e. 'att.innerAtt' will filter the items by 'innerAtt'
 * of attribute 'att' of each item.
 * Filter value 'all' means to skip the given filter.
 */
const DataFilter = {

    filterData: function (data, filter) {
        if (this._canSkipFilter(filter)) {
            return data;
        }
        return data.filter(function (item) {
            for (const key in filter) {
                if (filter[key] === 'all') {
                    continue;
                }
                const path = key.split('.');
                let value = item;
                for (let i = 0, len = path.length; i < len; i++) {
                    value = value[path[i]];
                }
                if ((Array.isArray(value) && value.indexOf(filter[key]) === -1) || (!Array.isArray(value) && value !== filter[key])) {
                    return false;
                }
            }
            return true;
        });
    },

    _canSkipFilter: function (filter) {
        if (!filter) {
            return true;
        }
        for (const key in filter) {
            if (filter[key] !== 'all') {
                return false;
            }
        }
        return true;
    }
};

module.exports = DataFilter;