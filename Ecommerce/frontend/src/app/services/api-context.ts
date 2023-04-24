export abstract class ApiContext {
  public readonly BASE_URL = 'http://localhost:3232/';
  public readonly SOCKET = 'http://localhost:8388/';


  public readonly EDITOR_URL = '';
  public readonly BASE_URL_EDITOR = '';
  
  public readonly COMMON_URL = 'common';
  public readonly AI_URL = 'http://10.252.10.240:5555/';

  public readonly api = {
    base: 'neo',
    user: 'user',
    userType: 'userTypes',
    rule: 'rule',
  };
}