import {
  Component,
  OnInit,
  Input,
  ChangeDetectorRef,
  Output,
  EventEmitter,
} from "@angular/core";
import { FileUploader } from "ng2-file-upload";
import { AutoUploadService } from "../auto-upload.service";
import { TranslateService } from "@ngx-translate/core";
import { CoreTranslationService } from "@core/services/translation.service";
import { locale as eng } from "assets/languages/en";
import { locale as vie } from "assets/languages/vn";

@Component({
  selector: "app-collect-exhibition",
  templateUrl: "./collect-exhibition.component.html",
  styleUrls: ["./collect-exhibition.component.scss"],
})
export class CollectExhibitionComponent implements OnInit {
  // public
  public uploader: FileUploader;
  public listErrors = [];

  @Input() programId;
  @Output() reloadFolder = new EventEmitter<string>();

  constructor(
    private service: AutoUploadService,
    private ref: ChangeDetectorRef,
    private _coreTranslationService: CoreTranslationService,
    public _translateService: TranslateService
  ) {}

  /**
   * On init
   */
  ngOnInit(): void {
    this._coreTranslationService.translate(eng, vie);
    this.uploader = new FileUploader({
      isHTML5: true,
    });
  }

  createParentExhCollect() {
    
  }

  updateModal() {
    this.uploader.queue.forEach((element) => {
      this.service
        .collectExhibition(
          { content: { programId: this.programId } },
          element._file
        )
        .then((data) => {
          if (data.body.code === 0) {
            element.isUploaded = true;
            element.isSuccess = true;
            element.progress = 100;
          } else {
            element.isError = true;
            element.progress = 100;
            this.listErrors.push(
              this._translateService.instant("MESSAGE.COMMON.FILE_EXISTED") +
                element._file.name
            );
          }
          if (element == this.uploader.queue[this.uploader.queue.length - 1]) {
            this.uploader.progress = 100;
            this.reloadFolder.emit();
          }
        });
    });
  }

  mySwitch;
  changeCheckbox(e) {
    if (e.target.checked) {
      this.mySwitch = true;
    } else {
      this.mySwitch = false;
    }
  }

  onFolderSelected(e) {
    console.log("folder ... ", this.uploader);
    // var theFiles = e.srcElement.__ngContext__[31].uploader.queue[0]._file;
    // var relativePath = theFiles.webkitRelativePath;
    // console.log("đây là path :", relativePath);
    // var folder = relativePath.split("/");
    // console.log("đây là tên folder :", folder[0]);
    // console.log("uploader ... : ",     this.uploader.queue)
  }
}
