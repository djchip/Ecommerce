import { Injectable } from '@angular/core';
import { HttpUtilService } from 'app/services/http-util.service';
import * as SockJS from 'sockjs-client';
import {Stomp} from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private readonly SOCKET = this.httpUtilService.SOCKET;
  private stompClient = null;
  disabled = true;
  private notifyFunction: (notifyTopic:any) => void;

  constructor(
    private httpUtilService: HttpUtilService
  ) { 
    // this.connect();
  }

  setConnected(connected: boolean) {
    this.disabled = !connected;
  }

  connect(fn: (notifyTopic) => void) {
    const socket = new SockJS(this.SOCKET + 'chatbot-endpoint');
    this.stompClient = Stomp.over(socket);

    const _this = this;
    this.stompClient.connect({}, function (frame) {
      _this.setConnected(true);
      console.log('Connected: ' + frame);

      _this.stompClient.subscribe('/topic/notify-to-handler', function (notifyTopic) {
        fn(notifyTopic);
      });
    });
  }

  showNotifications(fn: (notifyTopic:any) => void) {
    this.notifyFunction = fn;
    // from now on, call myFunc wherever you want inside this service
    
  }

  disconnect() {
    if (this.stompClient != null) {
      this.stompClient.disconnect();
    }
    this.setConnected(false);
    console.log('Disconnected!');
  }

  sendMessage(message: string, username: string) {
    this.stompClient.send(
      '/chatbot/notify-to-handler',
      {},
      JSON.stringify({ 'message': message, 'username': username })
    );
  }
  
}
