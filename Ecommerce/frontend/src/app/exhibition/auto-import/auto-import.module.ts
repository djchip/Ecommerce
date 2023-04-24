import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { CoreCommonModule } from '@core/common.module';
import { AutoImportComponent } from './auto-import.component';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { TreeModule } from '@circlon/angular-tree-component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { FileUploadModule } from 'ng2-file-upload';
import { NgSelectModule } from '@ng-select/ng-select';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentHeaderModule } from "../../layout/components/content-header/content-header.module";

const routes: Routes = [
  {
    path: 'auto-import',
    component: AutoImportComponent,
  }
];

@NgModule({
    declarations: [
        AutoImportComponent
    ],
    imports: [
        CommonModule,
        RouterModule.forChild(routes),
        TranslateModule,
        NgSelectModule,
        FileUploadModule,
        FormsModule,
        NgbModule,
        TreeModule,
        CoreDirectivesModule,
        CorePipesModule,
        ContentHeaderModule,
        CoreCommonModule,
        NgxDatatableModule,
    ],
    exports: [
      AutoImportComponent
    ]
})
export class AutoImportModule { }
