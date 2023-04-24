import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-search-proof',
  templateUrl: './search-proof.component.html',
  styleUrls: ['./search-proof.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class SearchProofComponent implements OnInit {

  url: string = 'https://www.google.com/';
  urlSafe: SafeResourceUrl;

  constructor(public sanitizer: DomSanitizer) {}

  ngOnInit() {
    this.urlSafe = this.sanitizer.bypassSecurityTrustResourceUrl(this.url);
  }

  // downloadComplete(event){
  //   console.log("EVENT : ", event)
  // }

}
