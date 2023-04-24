export class Question {
  questionName: string;
  entity: Entity[]
  constructor() {
    {
      this.questionName = "";
      this.entity = [];

    }
  }
}

export class Entity {
  content: string;
  entityName: string;
  botSynonymPy: BotSynonymPy[]

  constructor() {
    {
      this.content = "";
      this.entityName = '';
      this.botSynonymPy = [];

    }
  }
}


export class BotSynonymPy {
  synonymContent: string

  constructor() {
    {
      this.synonymContent = "";

    }
  }
}




