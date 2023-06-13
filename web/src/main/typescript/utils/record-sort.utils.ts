export class RecordSortUtils {
    public static mergeSort(property: string, sources: Array<Record<string, string>>): Array<Record<string, string>> {
        const left = 0;
        const right = sources.length - 1;
        const sorted: Array<Record<string, string>> = [];
        for (const record of sources) {
            sorted.push(toRaw(record));
        }
        return this.merge(property, sorted, left, right);
    }

    private static merge(property: string, sources: Array<Record<string, string>>, left: number, right: number): Array<Record<string, string>> {
        if (left >= right) {
            return sources;
        }
        const mid = left + Math.floor((right - left) / 2);
        sources = RecordSortUtils.merge(property, sources, left, mid);
        sources = RecordSortUtils.merge(property, sources, mid + 1, right);
        sources = this.mergeUnsorted(property, sources, left, mid, right);
        return sources;
    } 

    private static mergeUnsorted(property: string, sources: Array<Record<string, string>>, left: number, mid: number, right: number): Array<Record<string, string>> {
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
            const l = new String(leftArray[firstIndex][property]).valueOf();
            const r = new String(rightArray[secondIndex][property]).valueOf();
            const compared = this.compare(l, r);
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

    private static compare(left: string, right: string): boolean {
        const numberLeft = new Number(left).valueOf();
        const numberRight = new Number(right).valueOf();
        return (left !== '' && right !== '' && !isNaN(numberLeft) && !isNaN(numberRight)) ? numberLeft <= numberRight : left.localeCompare(right) <= 0;
    }
}