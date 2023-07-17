import {MessageSetting} from "~/types/message-setting";

export const useMessageSettingStore = defineStore('message-setting', () => {
    const initState = (): MessageSetting => {
        return {
            id: '',
            value: ''
        };
    };

    const selectedMessage = ref<MessageSetting>(initState());

    const getMessageSetting = computed(() => selectedMessage.value);

    function setMessageSetting(messageSetting: MessageSetting): void {
        selectedMessage.value = messageSetting;
    }

    function $reset(): void {
        selectedMessage.value = initState();
    }

    return {
        selectedMessage,
        getMessageSetting,
        setMessageSetting,
        $reset
    };
});