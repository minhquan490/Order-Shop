import dataTableComponent from '~/components/DataTable.vue';
import {TableData, TableHeader} from "~/types";

type DataTableComponent = InstanceType<typeof dataTableComponent>

const sliceData = (data: Array<TableData>, component: DataTableComponent): Array<any> => {
    component.table.source = data;
    const currentPage: number = component.table.currentPage;
    const itemsPerPage: number = component.table.itemsPerPage;
    return data.slice(itemsPerPage * (currentPage - 1), currentPage * itemsPerPage);
}

const calculatePage = (data: Array<TableHeader>, component: DataTableComponent): number => {
    const itemsPerPage: number = component.table.itemsPerPage;
    let totalPage: number = data.length === 0 ? 0 : Math.floor(data.length / itemsPerPage);
    if (data.length % itemsPerPage !== 0) {
        totalPage = totalPage + 1;
    }
    return totalPage;
}

const filter = (event: Event, component: DataTableComponent): void => {
    event.preventDefault();
    const element: HTMLInputElement = event.target as HTMLInputElement;
    const searchValue: string = element.value;
    if (searchValue.length === 0) {
        component.table.source = sliceData(component.tableData, component);
        component.table.totalPage = calculatePage(component.tableData, component);
        return;
    }
    const result = filterByKeyword(searchValue, component);
    component.table.source = result.data;
    component.table.totalPage = result.totalPage;
}

const calculatePaginate = (currentPage: number, totalPage: number): Array<{ page: string, disabled: boolean }> => {
    const paginate: Array<{ page: string, disabled: boolean }> = [];
    if (totalPage <= 4) {
        for (let i = 1; i <= totalPage; i++) {
            paginate.push({page: i.toString(), disabled: i === currentPage});
        }
        return paginate;
    } else {
        if (currentPage > 3) {
            paginate.push({page: "...", disabled: false});
        }
        if (currentPage - 3 < 1) {
            for (let i = 1; i <= 3; i++) {
                paginate.push({page: i.toString(), disabled: currentPage === i});
            }
        } else {
            if (currentPage === totalPage - 1) {
                for (let i = 2; i > 0; i--) {
                    paginate.push({page: (currentPage - i).toString(), disabled: false});
                }
                paginate.push({page: currentPage.toString(), disabled: true});
            } else {
                if (currentPage === totalPage) {
                    for (let i = 3; i > 0; i--) {
                        paginate.push({page: (currentPage - i).toString(), disabled: false});
                    }
                    paginate.push({page: currentPage.toString(), disabled: true});
                } else {
                    paginate.push({page: (currentPage - 1).toString(), disabled: false});
                    paginate.push({page: currentPage.toString(), disabled: true});
                }
            }
        }
        if (currentPage < (totalPage - 1)) {
            paginate.push({page: "...", disabled: false});
        }
        if (currentPage !== totalPage) {
            paginate.push({page: totalPage.toString(), disabled: false});
        }
        return paginate;
    }
}

const goLeft = (component: DataTableComponent): void => {
    if (component.table.currentPage === 1 || component.table.source.length === 0) {
        return;
    }
    component.table.currentPage = parseInt(component.table.currentPage.toString()) - 1;
    component.table.source = sliceData(component.tableData, component);
    component.table.paginate = calculatePaginate(component.table.currentPage, component.table.totalPage);
}

const goRight = (component: DataTableComponent): void => {
    if (component.table.currentPage === component.table.totalPage || component.table.source.length === 0) {
        return;
    }
    component.table.currentPage = parseInt(component.table.currentPage.toString()) + 1;
    component.table.source = sliceData(component.tableData, component);
    component.table.paginate = calculatePaginate(component.table.currentPage, component.table.totalPage);
}

const resetButtons = (event: Event, buttons: any): void => {
    [...buttons].forEach(button => {
        if (button !== event.target) {
            button.removeAttribute("data-dir");
        }
    });
};

const mergeUnsorted = (property: string, sources: Array<Record<string, string>>, left: number, mid: number, right: number): Array<Record<string, string>> => {
    const n1 = mid - left + 1;
    const n2 = right - mid;

    const leftArray = new Array<Record<string, string>>(n1);
    const rightArray = new Array<Record<string, string>>(n2);

    for (let i = 0; i < n1; i++) {
        leftArray[i] = sources[left + i];
    }
    for (let i = 0; i < n2; i++) {
        rightArray[i] = sources[mid + 1 + i];
    }

    let firstIndex = 0;
    let secondIndex = 0;
    let mergedIndex = left;

    while (firstIndex < n1 && secondIndex < n2) {
        const l: string = String(leftArray[firstIndex][property]).valueOf();
        const r: string = String(rightArray[secondIndex][property]).valueOf();
        const compared: boolean = compare(l, r);
        if (compared) {
            sources[mergedIndex] = leftArray[firstIndex];
            firstIndex++;
        } else {
            sources[mergedIndex] = rightArray[secondIndex];
            secondIndex++;
        }
        mergedIndex++;
    }

    while (firstIndex < n1) {
        sources[mergedIndex] = leftArray[firstIndex];
        firstIndex++;
        mergedIndex++;
    }

    while (secondIndex < n2) {
        sources[mergedIndex] = rightArray[secondIndex];
        secondIndex++;
        mergedIndex++
    }

    return sources;
}

const mergeSort = (property: string, sources: Array<Record<string, string>>): Array<Record<string, string>> => {
    const left = 0;
    const right = sources.length - 1;
    const sorted: Array<Record<string, string>> = [];
    for (const record of sources) {
        sorted.push(toRaw(record));
    }
    return merge(property, sorted, left, right);
}

const merge = (property: string, sources: Array<Record<string, string>>, left: number, right: number): Array<Record<string, string>> => {
    if (left >= right) {
        return sources;
    }
    const mid: number = left + Math.floor((right - left) / 2);
    sources = merge(property, sources, left, mid);
    sources = merge(property, sources, mid + 1, right);
    sources = mergeUnsorted(property, sources, left, mid, right);
    return sources;
}

const compare = (left: string, right: string): boolean => {
    const numberLeft: number = Number(left).valueOf();
    const numberRight: number = Number(right).valueOf();
    return (left !== '' && right !== '' && !isNaN(numberLeft) && !isNaN(numberRight)) ? numberLeft <= numberRight : left.localeCompare(right) <= 0;
}

const sort = (component: DataTableComponent, event: Event, header: string): void => {
    let source = component.table.source;
    if (Array.isArray(source?.[0][header])) {
        return;
    }
    const buttons: Element[] = toRaw(component.$refs['table-head-button']) as Array<Element>;
    resetButtons(event, buttons);
    const element: Element = event.target as Element;
    if (element.getAttribute("data-dir") == "desc") {
        source = source.reverse();
        element.setAttribute('data-dir', 'asc');
    } else {
        source = mergeSort(header, source as Array<Record<string, any>>);
        element.setAttribute('data-dir', 'desc');
    }
    component.table.source = source;
    component.table.data = sliceData(source, component.table);
}

const filterByCategories = (event: Event, component: DataTableComponent): void => {
    const target: HTMLInputElement = event.target as HTMLInputElement;
    if (target.checked) {
        component.selectedCategories.push(target.value);
    } else {
        const categories: Array<string> = component.selectedCategories;
        component.selectedCategories = categories.filter((value: string): boolean => value !== target.value);
    }
    if (component.selectedCategories.length === 0) {
        return;
    } else {
        const filteredData: ReturnType<typeof filterByKeyword> = {
            totalPage: 0,
            data: []
        };
        const categories: Array<string> = component.selectedCategories;
        categories.forEach(category => {
            const processedData = filterByKeyword(category, component, 'categories');
            filteredData.data.push(processedData.data);
            filteredData.totalPage = filteredData.totalPage + processedData.totalPage;
        });
        component.table.source = filteredData.data;
        component.table.totalPage = filteredData.totalPage;
    }
}

const filterByKeyword = (keyword: string, component: DataTableComponent, property?: string): {
    data: TableData[],
    totalPage: number
} => {
    const searchValue: string = keyword;
    let filteredData;
    if (property) {
        filteredData = component.tableData.filter(value => {
            const resolvedObject = toRaw(value);
            const proxyData = resolvedObject[property];
            const data: string = String(proxyData);
            const locale: string = navigator.languages[0];
            if (data.toLocaleUpperCase(locale).indexOf(searchValue.toLocaleUpperCase(locale)) > -1) {
                return value;
            }
        });
    } else {
        filteredData = component.tableData.filter(value => {
            for (const header of component.headers) {
                const resolvedObject = toRaw(value);
                const proxyData = resolvedObject[header.dataPropertyName];
                const data: string = String(proxyData);
                const locale: string = navigator.languages[0];
                if (data.toLocaleUpperCase(locale).indexOf(searchValue.toLocaleUpperCase(locale)) > -1) {
                    return value;
                }
            }
        });
    }
    component.table.source = sliceData(filteredData, component);
    component.table.totalPage = calculatePage(filteredData, component);
    return {
        data: sliceData(filteredData, component),
        totalPage: calculatePage(filteredData, component)
    }
}

export {
    filter,
    calculatePaginate,
    calculatePage,
    sliceData,
    goLeft,
    goRight,
    sort,
    filterByCategories
}