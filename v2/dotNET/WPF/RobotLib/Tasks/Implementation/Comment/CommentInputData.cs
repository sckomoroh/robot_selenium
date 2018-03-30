using Robot.Tasks.Common;

namespace Robot.Tasks.Implementation.Comment
{
    public class CommentInputData : ActionTaskInputData
    {
        public string UserWebReference { get; set; }

        public string Comment { get; set; }
    }
}
