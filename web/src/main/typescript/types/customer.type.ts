export type Customer = {
  id: string,
  first_name: string,
  last_name: string,
  phone: string,
  email: string,
  gender: string,
  role: string,
  username: string,
  address: Array<string>,
  is_activated: boolean,
  is_account_non_expired: boolean,
  is_account_non_locked: boolean,
  is_credentials_non_expired: boolean,
  is_enabled: boolean,
  picture: string
};
