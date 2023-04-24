import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { CollectExhibitionComponent } from './collect-exhibition/collect-exhibition.component';
import { TranslateModule } from '@ngx-translate/core';
import { NgSelectModule } from '@ng-select/ng-select';
import { FileUploadModule } from 'ng2-file-upload';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { TreeModule } from '@circlon/angular-tree-component';
import { CorePipesModule } from '@core/pipes/pipes.module';
import { CoreDirectivesModule } from '@core/directives/directives';
import { AutoUploadComponent } from './auto-upload/auto-upload.component';

const routes: Routes = [
  {
    path: 'list',
    component: AutoUploadComponent,
  }
];

@NgModule({
  declarations: [
    AutoUploadComponent,
    CollectExhibitionComponent,
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
  ],
  exports: [
    AutoUploadComponent,
    CollectExhibitionComponent
  ]
})
export class AutoUploadModule { }
