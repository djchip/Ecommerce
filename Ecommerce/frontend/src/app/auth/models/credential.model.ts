export class CredentialModel {
  email: string;
  phone: string;
  userId: string;
  fullName: string;

  constructor(userName: string = '') {
    this.fullName = userName;
  }
}
