import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpRequest, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { DatePipe } from '@angular/common';
import { catchError, timeout } from 'rxjs/operators';
import { ApiContext } from './api-context';
import { TokenStorage } from './token-storage.service';

@Injectable({
  providedIn: 'root'
})
export class HttpUtilService extends ApiContext {

  constructor(
    public http: HttpClient, private tokenStorage: TokenStorage) {
    super();
  }

  public handleError(error) {
    return throwError(error);
  }

  public callAPI(url: string, data: any): Observable<any> {
    let method: any;
    if (data.method) {
      method = data.method;
      delete data.method;
    }

    let responseType: any = 'json';

    let headers: any;
    // truyen authorization len phuong thuc get
    if (data && data.authorizationParams) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + data.authorizationParams });
      headers.append('Content-Type', 'application/json; charset=utf-8');

    } else if (this.tokenStorage.getTokenStr()) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + this.tokenStorage.getTokenStr() });
      headers.append('Content-Type', 'application/json; charset=utf-8');

    }
    const requestParam = Object.assign({}, data);
    const fullDate = new Date();
    const datePipe = new DatePipe('en-US');
    const currentDate = datePipe.transform(fullDate, 'dd/MM/yyyy');
    let signature = '';
    const param = Object.keys(requestParam);
    for (let i = 0; i < param.length; i++) {
      if (requestParam[param[i]] || requestParam[param[i]] === 0) {
        signature = signature + requestParam[param[i]];
      }
    }
    signature = signature + 'web' + 'EJVsEmpnoqStUZbTSnEwdCpZsoGgIm' + currentDate;
    let params = {};
    let body = {};
    if (method === 'GET') {
      params = requestParam;
    } else {
      body = requestParam.content;
    }
    const ops = {
      body,
      headers,
      params,
      responseType
    };
    return this.http.request(method, url, ops).pipe(catchError(this.handleError)
    );
  }

  public callAPIUploadFilesAndData(url: string, files: File, param: any): Observable<any> {
    let method = 'POST';
    let responseType: any = 'json';
    let headers: any;
    if (param.method) {
      method = param.method;
      delete param.method;
    }
    // truyen authorization len phuong thuc get
    if (this.tokenStorage.getTokenStr()) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + this.tokenStorage.getTokenStr() });
      headers.append('Content-Type', 'application/json; charset=utf-8');
    }
    const formData: FormData = new FormData();
    // if (files) {
    //   for (var i = 0; i < files.length; i++) {
    //     formData.append('file', files[i]);
    //     console.log("file upload: " + files[i]);
    //   }
    // }
    if(files != null){
      formData.append('fileUpload', files);
    } else {
      formData.append('fileUpload', null);
    }
    
    if (param.content) {
      Object.keys(param.content).forEach(key => formData.append(key, param.content[key]));
    }
    const requestParam = Object.assign({}, param);
    let body = formData;
    const ops = {
      headers,
      body,
      responseType
    };
    // const req = new HttpRequest(method, url, formData, ops);
    return this.http.request(method, url, ops).pipe(catchError(this.handleError));
  }

  public callAPIUploadFilesAndDataOfProof(url: string, files: File, param: any): Observable<any> {
    let method = 'POST';
    let responseType: any = 'json';
    let headers: any;
    if (param.method) {
      method = param.method;
      delete param.method;
    }
    // truyen authorization len phuong thuc get
    if (this.tokenStorage.getTokenStr()) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + this.tokenStorage.getTokenStr() });
      headers.append('Content-Type', 'application/json; charset=utf-8');
    }
    const formData: FormData = new FormData();
    formData.append('attachments', files);
    if (param.content) {
      Object.keys(param.content).forEach(key => formData.append(key, param.content[key]));
    }
    const requestParam = Object.assign({}, param);
    let body = formData;
    const ops = {
      headers,
      body,
      responseType
    };
    return this.http.request(method, url, ops).pipe(catchError(this.handleError));
  }

  public callAPIUploadImg(url: string, files: FileList, param: any): Observable<any> {
    let method = 'POST';
    let responseType: any = 'json';
    let headers: any;
    if (param.method) {
      method = param.method;
      delete param.method;
    }
    // truyen authorization len phuong thuc get
    if (this.tokenStorage.getTokenStr()) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + this.tokenStorage.getTokenStr() });
      headers.append('Content-Type', 'application/json; charset=utf-8');
    }
    const formData: FormData = new FormData();
    for(let i = 0; i < files.length; i++){
      formData.append('imgProduct', files[i]);
    }
    if (param.content) {
      Object.keys(param.content).forEach(key => formData.append(key, param.content[key]));
    }
    const requestParam = Object.assign({}, param);
    let body = formData;
    const ops = {
      headers,
      body,
      responseType
    };
    return this.http.request(method, url, ops).pipe(catchError(this.handleError));
  }

  public downloadFile(url: string, fileName: string): void {
    let headers: any;
    if (this.tokenStorage.getTokenStr()) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + this.tokenStorage.getTokenStr() });
      headers.append('Content-Type', 'application/json; charset=utf-8');
    }
    this.http.get(this.BASE_URL + url, { headers, responseType: 'blob' as 'json' }).subscribe(
      (response: any) => {
        console.log(response);
        let dataType = response.type;
        let binaryData = [];
        binaryData.push(response);
        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: dataType }));
        document.body.appendChild(downloadLink);
        downloadLink.setAttribute('download', fileName);
        downloadLink.click();
      }
    )
  }


  public onDownloadFile(url: string, fileName: string): void {
    let headers: any;
    if (this.tokenStorage.getTokenStr()) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + this.tokenStorage.getTokenStr() });
      headers.append('Content-Type', 'application/json; charset=utf-8');
    }
    this.http.get(url, { headers, responseType: 'blob' as 'json' }).subscribe(
      (response: any) => {
        console.log(response);
        let dataType = response.type;
        let binaryData = [];
        binaryData.push(response);
        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: dataType }));
        document.body.appendChild(downloadLink);
        downloadLink.setAttribute('download', fileName);
        downloadLink.click();
        console.log(downloadLink.href);
        
      }
    )
  }

  public onDownloadFileLoad(url: string, fileName: string): Observable<any> {
    let headers: any;
    let res: any;
    if (this.tokenStorage.getTokenStr()) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + this.tokenStorage.getTokenStr() });
      headers.append('Content-Type', 'application/json; charset=utf-8');
    }
    this.http.get(url, { headers, responseType: 'blob' as 'json' }).subscribe(
      (response: any) => {
        res = response;
        let dataType = response.type;
        let binaryData = [];
        binaryData.push(response);
        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: dataType }));
        document.body.appendChild(downloadLink);
        downloadLink.setAttribute('download', fileName);
        downloadLink.click();
      }
    )
    return this.http.request("GET", url, { headers, responseType: 'blob' as 'json' }).pipe(catchError(this.handleError));
  }

  public onDownloadFileSelected(url: string, fileName: string, param: any): Observable<any> {
    let headers: any;
    let responseType: any = 'json';
    
    let method = 'GET';
    if (param.method) {
      method = param.method;
      delete param.method;
    }
    const formData: FormData = new FormData();
    if (param.content) {
      Object.keys(param.content).forEach(key => formData.append(key, param.content[key]));
    }
    const requestParam = Object.assign({}, param);
    let body = formData;
    const ops = {
      headers,
      body,
      responseType
    };

    if (this.tokenStorage.getTokenStr()) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + this.tokenStorage.getTokenStr() });
      headers.append('Content-Type', 'application/json; charset=utf-8');
    }
    this.http.get(url, { headers, responseType: 'blob' as 'json' }).subscribe(
      (response: any) => {
        console.log(response);
        let dataType = response.type;
        let binaryData = [];
        binaryData.push(response);
        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: dataType }));
        document.body.appendChild(downloadLink);
        downloadLink.setAttribute('download', fileName);
        downloadLink.click();
        console.log(downloadLink.href);
        
      }
    )
    return this.http.request(method, url, ops).pipe(catchError(this.handleError));
  }

  public exportUnitToExcel(url: string, fileName: string): void {
    let headers: any;
    if (this.tokenStorage.getTokenStr()) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + this.tokenStorage.getTokenStr() });
      headers.append('Content-Type', 'application/json; charset=utf-8');
    }
    this.http.get(url, { headers, responseType: 'blob' as 'json' }).subscribe(
      (response: any) => {
        console.log(response);
        let dataType = response.type;
        let binaryData = [];
        binaryData.push(response);
        let downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, { type: dataType }));
        document.body.appendChild(downloadLink);
        downloadLink.setAttribute('download', fileName);
        downloadLink.click();
      }
    )
  }

  public uploadMultipartFile(url: string, file: File, param: any): Observable<any> {
    let responseType: any = 'json';
    let headers: any;
    // truyen authorization len phuong thuc get
    if (this.tokenStorage.getTokenStr()) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + this.tokenStorage.getTokenStr() });
      headers.append('Content-Type', 'application/json; charset=utf-8');
    }
    const requestParam = Object.assign({}, param);
    let body = requestParam;
    let params = new HttpParams();
    params = param;
    const ops = {
      headers,
      body,
      params,
      responseType
    };
    const formData: FormData = new FormData();
    formData.append('file', file);
    const req = new HttpRequest('POST', this.BASE_URL + url, formData, ops);
    return this.http.request(req).pipe(timeout(300000)).pipe(catchError(this.handleError));
  }

  public uploadMultipartFileWithFormData(url: string, file: File, param: any): Observable<any> {
    let method = 'POST';
    let responseType: any = 'json';
    let headers: any;
    if (param.method) {
      method = param.method;
      delete param.method;
    }
    
    // truyen authorization len phuong thuc get
    if (this.tokenStorage.getTokenStr()) {
      headers = new HttpHeaders({ 'Authorization': 'Bearer ' + this.tokenStorage.getTokenStr() });
      headers.append('Content-Type', 'application/json; charset=utf-8');
    }
    const requestParam = Object.assign({}, param);
    let body = requestParam;
    const ops = {
      headers,
      body,
      responseType
    };
    const formData: FormData = new FormData();
    if (file) {
      formData.append('file', file);
    }
    if (param.content) {
      Object.keys(param.content).forEach(key => formData.append(key, param.content[key]));
    }
    const req = new HttpRequest(method, this.BASE_URL + url, formData, ops);
    return this.http.request(req).pipe(catchError(this.handleError));
  }

}
